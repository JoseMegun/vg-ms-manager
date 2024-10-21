package com.inventary.enriqueta.presentation.Controller;

import com.inventary.enriqueta.domain.Model.Manager;
import com.inventary.enriqueta.application.Service.impl.ManagerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("${api.version}/public/manager")
public class ManagerPublicController {

    private final ManagerServiceImpl managerService;

    @Autowired
    public ManagerPublicController(ManagerServiceImpl managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/welcome")
    public Mono<ResponseEntity<String>> getWelcomeMessage() {
        return managerService.getWelcomeMessage()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/actives")
    public Mono<ResponseEntity<Flux<Manager>>> getAllActiveManagers() {
        return Mono.just(ResponseEntity.ok(managerService.listAllActive()));
    }

    @GetMapping("/inactives")
    public Mono<ResponseEntity<Flux<Manager>>> getAllInactiveManagers() {
        return Mono.just(ResponseEntity.ok(managerService.listAllInactive()));
    }

    @GetMapping("/document/{documentNumber}")
    public Mono<ResponseEntity<Manager>> getManagerByDocumentNumber(@PathVariable String documentNumber) {
        return managerService.findByDocumentNumber(documentNumber)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Manager>> getManagerByEmail(@PathVariable String email) {
        return managerService.findByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/role")
    public Mono<ResponseEntity<Flux<Manager>>> getManagersByRole(@RequestParam String role) {
        return Mono.just(ResponseEntity.ok(managerService.findByRole(role)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Manager>> getManagerById(@PathVariable String id) {
        return managerService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Manager>> createManager(@RequestBody Manager manager) {
        return managerService.createManager(manager)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteManager(@PathVariable String id) {
        return managerService.deleteManager(id)
                .map(deleted -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/reactivate/{id}")
    public Mono<ResponseEntity<Manager>> reactivateManager(@PathVariable String id) {
        return managerService.reactivateManager(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Manager>> updateManager(@PathVariable String id, @RequestBody Manager managerDetails) {
        return managerService.updateManager(id, managerDetails)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/updatePassword/{id}")
    public Mono<ResponseEntity<Manager>> updatePassword(@PathVariable String id, @RequestBody String newPassword) {
        return managerService.updatePassword(id, newPassword)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
