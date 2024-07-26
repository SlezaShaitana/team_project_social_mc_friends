package com.social.mc_friends.service.impl;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.model.*;
import com.social.mc_friends.repository.*;
import com.social.mc_friends.service.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
//    private final UUID userId = UUID.randomUUID(); //Будет взят из токена
    private final UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
    private final RelationshipRepository relationshipRepository;
    private final OperationRepository operationRepository;
    @Override
    @Transactional
    public Relationship confirmFriendRequest(UUID relatedUserId) {
            Operation operation = createOperation(relatedUserId, OperationType.FRIENDSHIP_CONFIRMATION);
            Relationship relationship = makeRelationship(userId,  relatedUserId, StatusCode.FRIEND, operation);
            saveReverseRelationship(relatedUserId, userId, StatusCode.FRIEND, operation);
            return relationship;
    }

    @Override
    public Relationship unblockFriend(UUID relatedUserId) {
            Operation operation = createOperation(relatedUserId, OperationType.UNBLOCKING);
            return makeRelationship(userId, relatedUserId, StatusCode.FRIEND, operation);
    }

    @Override
    public Relationship blockFriend(UUID relatedUserId) {
            Operation operation = createOperation(relatedUserId, OperationType.BLOCKING);
            return makeRelationship(userId, relatedUserId, StatusCode.BLOCKED, operation);
    }

    @Override
    public Relationship createFriendRequest(UUID relatedUserId) {
        Operation operation = createOperation(relatedUserId, OperationType.FRIEND_REQUEST);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.REQUEST_TO, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.REQUEST_FROM, operation);
        return relationship;
    }

    @Override
    public Relationship subscribeToFriend(UUID relatedUserId) {
        Operation operation = createOperation(relatedUserId, OperationType.SUBSCRIPTION);

        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.SUBSCRIBED, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.WATCHING, operation);
        return relationship;
    }

    @Override
    public List<User> getFriendList(FriendSearchDto searchDto, Pageable page) {
        return null;
    }

    @Override
    public FriendShortDto getFriendshipNote(UUID uuid) {
        return null;
    }

    @Override
    public void deleteFriend(UUID uuid) {

    }

    @Override
    public List<UUID> getFriendsIdList(StatusCode status) {
        return null;
    }

    @Override
    public List<FriendShortDto> getRecommendations(FriendSearchDto searchDto) {
        return null;
    }

    @Override
    public List<UUID> getAllFriendsIdList() {
        return null;
    }

    @Override
    public List<UUID> getFriendsIdListByUserId(UUID userId) {
        return null;
    }

    @Override
    public Integer getFriendRequestCount() {
        return null;
    }

    @Override
    public List<StatusCode> getStatuses(List<UUID> ids) {
        return null;
    }

    @Override
    public List<UUID> getFriendsWhoBlockedUser() {
        return null;
    }

    private Operation createOperation(UUID relatedUserId, OperationType operationType){
        Operation operation = new Operation();
        operation.setUserId(userId);
        operation.setRelatedUserId(relatedUserId);
        operation.setOperationType(operationType);
        operationRepository.save(operation);
        return operation;
    }
    private Relationship makeRelationship(UUID userId, UUID relatedUserId, StatusCode  statusCode, Operation operation){
        Relationship relationship = relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
        if (relationship == null) {
            relationship = new Relationship();
            relationship.setUserId(userId);
            relationship.setRelatedUserId(relatedUserId);
        }
        StatusCode previousStatus = relationship.getStatusCode() == null ? StatusCode.NONE : relationship.getStatusCode();
        relationship.setStatusCode(statusCode);
        relationship.setPreviousStatusCode(previousStatus);
        relationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(relationship);
        return relationship;
    }

    private void saveReverseRelationship(UUID relatedUserId, UUID userId, StatusCode  statusCode, Operation operation){
        Relationship reverseRelationship = relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId);
        if (reverseRelationship == null) {
            reverseRelationship = new Relationship();
            reverseRelationship.setRelatedUserId(userId);
            reverseRelationship.setUserId(relatedUserId);
        }
        StatusCode previousStatus = reverseRelationship.getStatusCode() == null ? StatusCode.NONE : reverseRelationship.getStatusCode();
        reverseRelationship.setStatusCode(statusCode);
        reverseRelationship.setPreviousStatusCode(previousStatus);
        reverseRelationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(reverseRelationship);
    }
}

