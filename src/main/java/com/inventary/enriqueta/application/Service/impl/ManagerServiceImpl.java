package com.inventary.enriqueta.application.Service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.inventary.enriqueta.domain.Model.Manager;
import com.inventary.enriqueta.domain.Repository.ManagerRepository;
import com.inventary.enriqueta.application.Service.ManagerService;
import com.inventary.enriqueta.application.webClient.AuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.modelmapper.ModelMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static com.inventary.enriqueta.application.Util.ManagerUtil.ACTIVE;
import static com.inventary.enriqueta.application.Util.ManagerUtil.INACTIVE;

@Service
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final AuthServiceClient authServiceClient;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public ManagerServiceImpl(ManagerRepository managerRepository, AuthServiceClient authServiceClient) {
        this.managerRepository = managerRepository;
        this.authServiceClient = authServiceClient;
    }

    public Mono<String> getWelcomeMessage() {
        return Mono.just("Bienvenidos al microservicio de encargado");
    }

    @Override
    public Flux<Manager> listAllActive() {
        return managerRepository.findByStatus(ACTIVE);
    }

    @Override
    public Flux<Manager> listAllInactive() {
        return managerRepository.findByStatus(INACTIVE);
    }

    @Override
    public Mono<Manager> findByDocumentNumber(String documentNumber) {
        return managerRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    public Mono<Manager> findByEmail(String email) {
        return managerRepository.findByEmail(email);
    }

    @Override
    public Mono<Manager> findById(String id) {
        return managerRepository.findById(id);
    }

    @Override
    public Flux<Manager> findByRole(String role) {
        return managerRepository.findByRoleIgnoreCase(role);
    }

    @Override
    public Mono<Manager> createManager(Manager manager) {
        Manager newManager = modelMapper.map(manager, Manager.class);
        newManager.setStatus(ACTIVE);
        newManager.setCreatedAt(LocalDateTime.now());
        newManager.setUpdatedAt(LocalDateTime.now());

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(manager.getEmail())
                .setPassword(manager.getDocumentNumber())
                .setDisplayName(manager.getFirstName() + " " + manager.getLastName());

        return Mono.fromCallable(() -> FirebaseAuth.getInstance().createUser(request))
                .flatMap(userRecord -> {
                    newManager.setUid(userRecord.getUid());
                    newManager.setPassword(manager.getDocumentNumber());

                    // Asignar claims personalizados basados en el rol
                    try {
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("role", manager.getRole());

                        FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
                    } catch (Exception e) {
                        System.err.println("Error setting custom claims: " + e.getMessage());
                        return Mono.error(e);
                    }

                    return managerRepository.save(newManager);
                })
                .onErrorResume(e -> {
                    System.err.println("Error creating manager in Firebase: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<Manager> deleteManager(String id) {
        return managerRepository.findById(id)
                .flatMap(existingManager -> {
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingManager.getUid())
                                    .setDisabled(true)
                    )).then(Mono.defer(() -> {
                        existingManager.setStatus(INACTIVE);
                        return managerRepository.save(existingManager);
                    }));
                });
    }

    @Override
    public Mono<Manager> reactivateManager(String id) {
        return managerRepository.findById(id)
                .flatMap(existingManager -> {
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingManager.getUid())
                                    .setDisabled(false)
                    )).flatMap(userRecord -> {
                        existingManager.setStatus(ACTIVE);
                        return managerRepository.save(existingManager);
                    });
                });
    }

    @Override
    public Mono<Manager> updateManager(String id, Manager managerDetails) {
        return managerRepository.findById(id)
                .flatMap(existingManager -> {
                    existingManager.setFirstName(managerDetails.getFirstName());
                    existingManager.setLastName(managerDetails.getLastName());
                    existingManager.setDocumentType(managerDetails.getDocumentType());
                    existingManager.setDocumentNumber(managerDetails.getDocumentNumber());
                    existingManager.setGender(managerDetails.getGender());
                    existingManager.setAddress(managerDetails.getAddress());
                    existingManager.setBirthPlace(managerDetails.getBirthPlace());
                    existingManager.setEmail(managerDetails.getEmail());
                    existingManager.setRole(managerDetails.getRole());

                    existingManager.setUpdatedAt(LocalDateTime.now());

                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingManager.getUid())
                                    .setDisplayName(managerDetails.getFirstName() + " " + managerDetails.getLastName())
                    )).then(managerRepository.save(existingManager));
                });
    }

    @Override
    public Mono<Manager> updatePassword(String id, String newPassword) {
        return managerRepository.findById(id)
                .flatMap(existingManager -> {
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingManager.getUid()).setPassword(newPassword)
                    )).flatMap(userRecord -> {
                        existingManager.setPassword(newPassword);
                        return managerRepository.save(existingManager);
                    });
                })
                .onErrorResume(e -> {
                    System.err.println("Error updating password in Firebase: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    // Método para validar el token y el rol en el servicio
    public Mono<Boolean> validateTokenAndRoles(String token, List<String> requiredRoles) {
        return authServiceClient.validateToken(token)
                .flatMap(validationResponse -> {
                    if (validationResponse.isValid() && requiredRoles.contains(validationResponse.getRole())) {
                        return Mono.just(true); // Token válido y rol correcto
                    }
                    return Mono.just(false); // Token no válido o rol no coincide
                });
    }
}
