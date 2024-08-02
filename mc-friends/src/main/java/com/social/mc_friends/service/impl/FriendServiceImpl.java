package com.social.mc_friends.service.impl;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.mapper.Mapper;
import com.social.mc_friends.model.*;
import com.social.mc_friends.repository.*;
import com.social.mc_friends.repository.specificftions.FriendsSpecifications;
import com.social.mc_friends.service.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
//    private final UUID userId = UUID.randomUUID(); //Будет взят из токена
    private final UUID userId = UUID.fromString("a93a8071-f0b3-4ef8-9693-6fa668e9ea77");
    private final RelationshipRepository relationshipRepository;
    private final OperationRepository operationRepository;
    private final Mapper mapper;
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
    public Page<Relationship> getFriendList(FriendSearchDto searchDto, Integer page) {
        Specification<Relationship> spec = Specification.where(null);
        if (searchDto.getId() != null){
            spec = spec.and(FriendsSpecifications.operationIdEquals(UUID.fromString(searchDto.getId())));
        }
        if (searchDto.isDeleted()) {
            spec = spec.and(FriendsSpecifications.friendIsDelete(String.valueOf(StatusCode.NONE)));
        }
        if (searchDto.getIdFrom() != null){
            spec = spec.and(FriendsSpecifications.friendIdFromEquals(UUID.fromString(searchDto.getIdFrom())));
        }
        if (searchDto.getIdTo() != null){
            spec = spec.and(FriendsSpecifications.friendIdToEquals(UUID.fromString(searchDto.getIdTo())));
        }
        if (searchDto.getStatusCode() != null){
            spec = spec.and(FriendsSpecifications.friendsStatusEquals(StatusCode.valueOf(searchDto.getStatusCode())));
        }
        if (searchDto.getPreviousStatusCode() != null){
            spec = spec.and(FriendsSpecifications.friendsPreviousStatusEquals(StatusCode.valueOf(searchDto.getStatusCode())));
        }
       return relationshipRepository.findAll(spec, PageRequest.of(page - 1, 3));
    }

    @Override
    public Relationship getFriendshipNote(UUID relatedUserId) {
        return relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
    }

    @Override
    public void deleteFriend(UUID relatedUserId) {
        Operation operation = createOperation(relatedUserId, OperationType.REMOVAL);
        Relationship relationship = relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
        StatusCode previousStatusCode = relationship.getStatusCode();
        relationship.setStatusCode(StatusCode.NONE);
        relationship.setPreviousStatusCode(previousStatusCode);
        relationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(relationship);

    }

    @Override
    public List<UUID> getUserIdList(String status) {
        return relationshipRepository.findByStatus(status);
    }

    @Override
    public List<UUID> getAllFriendsIdList() {//Для текущего пользователя
        return relationshipRepository.findAllFriendsId(userId);
    }

    @Override
    public List<UUID> getFriendsIdListByUserId(UUID id) {
        return relationshipRepository.findAllFriendsId(id);
    }

    @Override
    public Integer getFriendRequestCount() {
       List<Relationship> relationshipList = relationshipRepository.findByUserIdAndStatusCode(userId);
        return relationshipList.size();
    }

    @Override
    public List<StatusCode> getStatuses(List<UUID> ids) {
        return null;
    }

    @Override
    public List<UUID> getFriendsWhoBlockedUser() {
        return relationshipRepository.findBlockingFriendsId(userId);
    }
    @Override
    public List<Relationship> getRecommendations(FriendSearchDto searchDto) {
        return relationshipRepository.findAllFriends(UUID.fromString(searchDto.getIdTo()));
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

