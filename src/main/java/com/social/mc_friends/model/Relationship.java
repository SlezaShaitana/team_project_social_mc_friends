package com.social.mc_friends.model;

import com.social.mc_friends.dto.StatusCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relationship"/*,indexes = {@Index(name = "friendIndex", columnList = "user_id, related_user_id", unique = true)}*/)
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", name = "id", nullable = false)
    private UUID uuid;
    @Column(columnDefinition = "uuid", name = "user_id", nullable = false)
    private UUID userId;
    @Column(columnDefinition = "uuid", name = "related_user_id",nullable = false)
    private UUID relatedUserId;
    @Column(name = "status", columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCode statusCode;
    @Column(name = "previous_status", columnDefinition = "varchar(20)", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCode previousStatusCode;
    @Column(columnDefinition = "uuid", name = "status_change_id",nullable = false)
    private UUID statusChangeId;
    @Column(columnDefinition = "INT", nullable = true)
    private int rating;


}
