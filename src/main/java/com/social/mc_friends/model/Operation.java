package com.social.mc_friends.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Data
@Entity
@Table(name = "operations",indexes = {@Index(name = "blockingIndex", columnList = "user_id, related_id")})
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id", nullable = false)
    private UUID uuid;
    @Column(columnDefinition = "uuid", name = "user_id", nullable = false)
    private UUID userId;
    @Column(columnDefinition = "uuid", name = "related_id", nullable = false)
    private UUID relatedUserId;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)", name = "operation_type", nullable = false)
    private OperationType operationType;
}
