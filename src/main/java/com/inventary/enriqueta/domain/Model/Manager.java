package com.inventary.enriqueta.domain.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Data
@AllArgsConstructor
@Document(collection = "manager")
public class Manager {
    @Id
    private String id;
    private String uid;
    private String firstName;
    private String lastName;
    private String documentType;
    private String documentNumber;
    private String gender;
    private String address;
    private String birthPlace;
    private String email;
    private String role;            // Add this field
    private String password;        // Add this field
    private String status;
    private LocalDateTime createdAt;// Add this field
    private LocalDateTime updatedAt;// Add this field

}
