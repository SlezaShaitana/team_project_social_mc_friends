package com.social.mc_friends.repository;

import com.social.mc_friends.model.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {
    Relationship findByUserIdAndRelatedUserId(UUID userId, UUID relatedUserId);
}
