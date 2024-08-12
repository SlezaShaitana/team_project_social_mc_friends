package com.social.mc_friends.repository;

import com.social.mc_friends.dto.StatusCode;
import com.social.mc_friends.model.Relationship;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID>, JpaSpecificationExecutor<Relationship> {
    Relationship findByUserIdAndRelatedUserId(UUID userId, UUID relatedUserId);
    @Modifying
    @Query(nativeQuery = true, value = "SELECT user_id FROM relationship  WHERE status LIKE %:status%")
    List<UUID> findByStatus(@Param("status") String status);
    @Modifying
    @Query(nativeQuery = true, value = "SELECT related_user_id FROM relationship  WHERE user_id = :user_id AND status = '0'")
    List<UUID> findAllFriendsId(@Param("user_id") UUID userId);

    @Modifying
    @Query(nativeQuery = true, value = "SELECT * FROM relationship  WHERE user_id = :user_id AND status = '2'")
    List<Relationship> findByUserIdAndStatusCode(@Param("user_id") UUID userId);
    @Modifying
    @Query(nativeQuery = true, value = "SELECT user_id FROM relationship  WHERE related_user_id = :related_user_id AND status = '3'")
    List<UUID> findBlockingFriendsId(@Param("related_user_id") UUID userId);

    @Modifying
    @Query(nativeQuery = true, value = "SELECT * FROM relationship  WHERE user_id = :user_id AND status = '0'")
    List<Relationship> findAllFriends(@Param("user_id") UUID userId);




}
