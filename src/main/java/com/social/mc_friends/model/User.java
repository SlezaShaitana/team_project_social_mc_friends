package com.social.mc_friends.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @Column(columnDefinition = "uuid", name = "user_id", nullable = false)
    private UUID userId;
    @Column(columnDefinition = "varchar(255)", name = "password", nullable = false)
    private String email;
    @Column(columnDefinition = "varchar(255)", name = "first_name", nullable = true)
    private String firstName;
    @Column(columnDefinition = "varchar(255)", name = "last_name", nullable = true)
    private String lastName;
}