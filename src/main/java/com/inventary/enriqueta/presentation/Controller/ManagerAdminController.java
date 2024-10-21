package com.inventary.enriqueta.presentation.Controller;

import com.inventary.enriqueta.application.Service.impl.ManagerServiceImpl;
import com.inventary.enriqueta.domain.Model.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("${api.version}/directives/manager")
public class ManagerAdminController {

    private final ManagerServiceImpl managerService;

    @Autowired
    public ManagerAdminController(ManagerServiceImpl managerService) {
        this.managerService = managerService;
    }

    // Lista de roles permitidos
    private static final List<String> ALLOWED_ROLES = Arrays.asList("DEVELOP", "DIRECTOR");

    @GetMapping("/actives")
    public Mono<ResponseEntity<Flux<Manager>>> getAllActiveManagers(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extract the Bearer token
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(ResponseEntity.ok(managerService.listAllActive()));
                    } else {
                        return Mono.just(ResponseEntity.status(403).build()); // Access denied
                    }
                });
    }

    @GetMapping("/inactives")
    public Mono<ResponseEntity<Flux<Manager>>> getAllInactiveManagers(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(ResponseEntity.ok(managerService.listAllInactive()));
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @GetMapping("/document/{documentNumber}")
    public Mono<ResponseEntity<Manager>> getManagerByDocumentNumber(@PathVariable String documentNumber, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.findByDocumentNumber(documentNumber)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Manager>> getManagerByEmail(@PathVariable String email, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.findByEmail(email)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Manager>> getManagerById(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.findById(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Manager>> createManager(@RequestBody Manager manager, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.createManager(manager)
                                .map(ResponseEntity::ok);
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteManager(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.deleteManager(id)
                                .map(deleted -> ResponseEntity.noContent().<Void>build())
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PutMapping("/reactivate/{id}")
    public Mono<ResponseEntity<Manager>> reactivateManager(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.reactivateManager(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Manager>> updateManager(@PathVariable String id, @RequestBody Manager managerDetails, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.updateManager(id, managerDetails)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PatchMapping("/updatePassword/{id}")
    public Mono<ResponseEntity<Manager>> updatePassword(@PathVariable String id, @RequestBody String newPassword, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return managerService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return managerService.updatePassword(id, newPassword)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }
}
