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
@RequestMapping("${api.version}/shared/manager")
public class ManagerUserController {

    private final ManagerServiceImpl managerService;

    @Autowired
    public ManagerUserController(ManagerServiceImpl managerService) {
        this.managerService = managerService;
    }

    // Lista de roles permitidos
    private static final List<String> ALLOWED_ROLES = Arrays.asList("DEVELOP", "SECRETARIO", "INVENTARIO");

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
