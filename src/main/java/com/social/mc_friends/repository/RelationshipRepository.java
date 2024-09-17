package com.social.mc_friends.repository;

import com.social.mc_friends.model.Relationship;
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
    @Query(nativeQuery = true, value = "SELECT related_user_id FROM relationship  WHERE user_id = :user_id AND status LIKE '%FRIEND%'")
    List<UUID> findAllFriendsId(@Param("user_id") UUID userId);

    @Modifying
    @Query(nativeQuery = true, value = "SELECT * FROM relationship  WHERE user_id = :user_id AND status LIKE '%REQUEST_FROM%'")
    List<Relationship> findByUserIdAndStatusCode(@Param("user_id") UUID userId);
    @Modifying
    @Query(nativeQuery = true, value = "SELECT user_id FROM relationship  WHERE related_user_id = :related_user_id AND LIKE '%BLOCKED%'")
    List<UUID> findBlockingFriendsId(@Param("related_user_id") UUID userId);

    @Modifying
    @Query(nativeQuery = true, value = "SELECT * FROM relationship  WHERE user_id = :user_id AND status LIKE '%FRIEND%'")
    List<Relationship> findAllFriends(@Param("user_id") UUID userId);




}
