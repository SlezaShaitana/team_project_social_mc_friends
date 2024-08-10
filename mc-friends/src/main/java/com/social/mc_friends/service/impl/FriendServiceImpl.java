package com.social.mc_friends.service.impl;


import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.model.*;
import com.social.mc_friends.repository.*;
import com.social.mc_friends.repository.specificftions.FriendsSpecifications;
import com.social.mc_friends.security.JwtTokenFilter;
import com.social.mc_friends.service.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final RelationshipRepository relationshipRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;
//    private final JwtTokenFilter jwtTokenFilter;
//    private final UUID userId = UUID.fromString("55fe221a-f4e7-49e4-83c1-bb63b0f67aa4");

     public static UUID userId;


    @Override
    @Transactional
    public Relationship confirmFriendRequest(UUID relatedUserId) throws UserException{
            log.info("confirmFriendRequest execution started");
            Operation operation = createOperation(relatedUserId, OperationType.FRIENDSHIP_CONFIRMATION);
            Relationship relationship = makeRelationship(userId,  relatedUserId, StatusCode.FRIEND, operation);
            saveReverseRelationship(relatedUserId, userId, StatusCode.FRIEND, operation);
            return relationship;
    }

    @Override
    public Relationship unblockFriend(UUID relatedUserId) throws UserException {
            log.info("unblockFriend execution started");
            Operation operation = createOperation(relatedUserId, OperationType.UNBLOCKING);
            return makeRelationship(userId, relatedUserId, StatusCode.FRIEND, operation);
    }

    @Override
    public Relationship blockFriend(UUID relatedUserId) throws UserException {
        log.info("blockFriend execution started");
            Operation operation = createOperation(relatedUserId, OperationType.BLOCKING);
            return makeRelationship(userId, relatedUserId, StatusCode.BLOCKED, operation);
    }

    @Override
    public Relationship createFriendRequest(UUID relatedUserId) throws UserException {
        log.info("createFriendRequest execution started");
        Operation operation = createOperation(relatedUserId, OperationType.FRIEND_REQUEST);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.REQUEST_TO, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.REQUEST_FROM, operation);
        return relationship;
    }

    @Override
    public Relationship subscribeToFriend(UUID relatedUserId) throws UserException {
        log.info("subscribeToFriend execution started");
        Operation operation = createOperation(relatedUserId, OperationType.SUBSCRIPTION);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.SUBSCRIBED, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.WATCHING, operation);
        return relationship;
    }

    @Override
    public Page<Relationship> getFriendList(FriendSearchDto searchDto, Integer page) {
        log.info("getFriendList execution started");
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
        log.info("getFriendshipNote execution started");
        return relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
    }

    @Override
    public void deleteFriend(UUID relatedUserId) {
        log.info("deleteFriend execution started");
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
        log.info("getUserIdList execution started");
        return relationshipRepository.findByStatus(status);
    }

    @Override
    public List<UUID> getAllFriendsIdList() {//Для текущего пользователя
        log.info("getAllFriendsIdList execution started");
        return relationshipRepository.findAllFriendsId(userId);
    }

    @Override
    public List<UUID> getFriendsIdListByUserId(UUID id) {
        log.info("getFriendsIdListByUserId execution started");
        return relationshipRepository.findAllFriendsId(id);
    }

    @Override
    public Integer getFriendRequestCount() {
        log.info("getFriendRequestCount execution started");
       List<Relationship> relationshipList = relationshipRepository.findByUserIdAndStatusCode(userId);
        return relationshipList.size();
    }

    @Override
    public List<StatusCode> getStatuses(List<UUID> ids) {
        return null;
    }

    @Override
    public List<UUID> getFriendsWhoBlockedUser() {
        log.info("getFriendsWhoBlockedUser execution started");
        return relationshipRepository.findBlockingFriendsId(userId);
    }
    @Override
    public List<Relationship> getRecommendations(FriendSearchDto searchDto) {
        log.info("getRecommendations execution started");
        return relationshipRepository.findAllFriends(UUID.fromString(searchDto.getIdTo()));
    }

    private Operation createOperation(UUID relatedUserId, OperationType operationType){
        log.info("createOperation execution started");
        Operation operation = new Operation();
        operation.setUserId(userId);
        operation.setRelatedUserId(relatedUserId);
        operation.setOperationType(operationType);
        operationRepository.save(operation);
        return operation;
    }
    private Relationship makeRelationship(UUID userId, UUID relatedUserId, StatusCode  statusCode, Operation operation) throws UserException {
        log.info("makeRelationship execution started");
        if(UserExists(userId) || UserExists(relatedUserId)){
            throw new UserException("User with ID " + userId + " or user with ID " + relatedUserId + " does not exist");
        }
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

    private void saveReverseRelationship(UUID relatedUserId, UUID userId, StatusCode  statusCode, Operation operation) throws UserException {
        log.info("saveReverseRelationship execution started");
        if(UserExists(userId) || UserExists(relatedUserId)){
            throw new UserException("User with ID " + userId + " or user with ID " + relatedUserId + " does not exist");
        }
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
    private boolean UserExists(UUID userId){
        return !userRepository.findById(userId).isPresent();
    }
}

