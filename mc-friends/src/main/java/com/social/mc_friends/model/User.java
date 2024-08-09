package com.social.mc_friends.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(columnDefinition = "uuid", name = "user_id", nullable = false)
    private UUID userId;
    @Column(columnDefinition = "varchar(255)", name = "password", nullable = false)
    private String password;
}