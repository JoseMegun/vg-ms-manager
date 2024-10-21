package com.inventary.enriqueta.application.Service;

import com.inventary.enriqueta.domain.Model.Manager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManagerService {
    Flux<Manager> listAllActive();
    Flux<Manager> listAllInactive();
    Mono<Manager> findByDocumentNumber(String documentNumber);
    Mono<Manager> findByEmail(String email);
    Mono<Manager> findById(String id);
    Mono<Manager> createManager(Manager manager);
    Mono<Manager> deleteManager(String id);
    Mono<Manager> reactivateManager(String id);
    Mono<Manager> updateManager(String id, Manager managerDetails);
    Mono<Manager> updatePassword(String id, String newPassword);
    Flux<Manager> findByRole(String role);
}
