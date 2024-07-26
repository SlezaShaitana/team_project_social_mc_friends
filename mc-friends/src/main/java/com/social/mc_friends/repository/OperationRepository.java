package com.social.mc_friends.repository;

import com.social.mc_friends.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
    Operation findByUserIdAndRelatedUserId(UUID userId, UUID relatedUserId);
}
