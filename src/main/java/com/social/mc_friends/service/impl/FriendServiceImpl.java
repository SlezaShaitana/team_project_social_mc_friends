package com.social.mc_friends.service.impl;


import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.kafka.KafkaProducer;
import com.social.mc_friends.model.*;
import com.social.mc_friends.repository.*;
import com.social.mc_friends.repository.specificftions.FriendsSpecifications;
import com.social.mc_friends.security.JwtUtils;
import com.social.mc_friends.service.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final RelationshipRepository relationshipRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public Relationship confirmFriendRequest(String token, UUID relatedUserId) throws UserException{
            log.info("confirmFriendRequest execution started");
            UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
            Operation operation = createOperation(userId, relatedUserId, OperationType.FRIENDSHIP_CONFIRMATION);
            Relationship relationship = makeRelationship(userId,  relatedUserId, StatusCode.FRIEND, operation);
            saveReverseRelationship(relatedUserId, userId, StatusCode.FRIEND, operation);
            kafkaProducer.sendMessageForFriendConformation(new MessageForKafkaDto(userId, relatedUserId));
            return relationship;
    }

    @Override
    public Relationship unblockFriend(String token, UUID relatedUserId) throws UserException {
            log.info("unblockFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
            Operation operation = createOperation(userId, relatedUserId, OperationType.UNBLOCKING);
            return makeRelationship(userId, relatedUserId, StatusCode.FRIEND, operation);
    }

    @Override
    public Relationship blockFriend(String token, UUID relatedUserId) throws UserException {
        log.info("blockFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
            Operation operation = createOperation(userId, relatedUserId, OperationType.BLOCKING);
            return makeRelationship(userId, relatedUserId, StatusCode.BLOCKED, operation);
    }

    @Override
    public Relationship createFriendRequest(String token, UUID relatedUserId) throws UserException {
        log.info("createFriendRequest execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Operation operation = createOperation(userId, relatedUserId, OperationType.FRIEND_REQUEST);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.REQUEST_TO, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.REQUEST_FROM, operation);
        kafkaProducer.sendMessageForFriendRequest(new MessageForKafkaDto(userId, relatedUserId));
        return relationship;
    }

    @Override
    public Relationship subscribeToFriend(String token, UUID relatedUserId) throws UserException {
        log.info("subscribeToFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Operation operation = createOperation(userId, relatedUserId, OperationType.SUBSCRIPTION);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.SUBSCRIBED, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.WATCHING, operation);
        return relationship;
    }

    @Override
    public Page<Relationship> getFriendList(String token, FriendSearchDto searchDto, Integer page) {
        log.info("getFriendList execution started");
        if (searchDto == null) {
            return new PageImpl<>(new ArrayList<>());
        }
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
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
    public Relationship getFriendshipNote(String token, UUID relatedUserId) {
        log.info("getFriendshipNote execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        return relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
    }

    @Override
    public void deleteFriend(String token, UUID relatedUserId) {
        log.info("deleteFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Operation operation = createOperation(userId, relatedUserId, OperationType.REMOVAL);
        Relationship relationship = relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
        StatusCode previousStatusCode = relationship.getStatusCode();
        relationship.setStatusCode(StatusCode.NONE);
        relationship.setPreviousStatusCode(previousStatusCode);
        relationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(relationship);
    }

    @Override
    public List<UUID> getUserIdList(String token, String status) {
        log.info("getUserIdList execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        return relationshipRepository.findByStatus(status);
    }

    @Override
    public List<UUID> getAllFriendsIdList(String token) {//Для текущего пользователя
        log.info("getAllFriendsIdList execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        return relationshipRepository.findAllFriendsId(userId);
    }

    @Override
    public List<UUID> getFriendsIdListByUserId(UUID id) {
        log.info("getFriendsIdListByUserId execution started");
        return relationshipRepository.findAllFriendsId(id);
    }

    @Override
    public Integer getFriendRequestCount(String token) {
        log.info("getFriendRequestCount execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
       List<Relationship> relationshipList = relationshipRepository.findByUserIdAndStatusCode(userId);
        return relationshipList.size();
    }

    @Override
    public List<StatusCode> getStatuses(List<UUID> ids) {
        return null;
    }

    @Override
    public List<UUID> getFriendsWhoBlockedUser(String token) {
        log.info("getFriendsWhoBlockedUser execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        return relationshipRepository.findBlockingFriendsId(userId);
    }
//    @Override
//    public List<Relationship> getRecommendations(FriendSearchDto searchDto) {
//        log.info("getRecommendations execution started");
//        return new ArrayList<>();
//        //return relationshipRepository.findAllFriends(UUID.fromString(searchDto.getIdTo()));
//    }
    @Override
    public List<User> getRecommendations(FriendSearchDto searchDto) {
        log.info("getRecommendations execution started");
        return userRepository.findAll();
    }

    private Operation createOperation(UUID userId, UUID relatedUserId, OperationType operationType){
        log.info("createOperation execution started");
        Operation operation = new Operation();
        operation.setUserId(userId);
        operation.setRelatedUserId(relatedUserId);
        operation.setOperationType(operationType);
        operationRepository.save(operation);
        return operation;
    }
    private Relationship makeRelationship(UUID userId, UUID relatedUserId, StatusCode  statusCode, Operation operation) throws UserException {
        log.info("makeRelationship execution started. UserId: " + userId + " relatedUserId: " + relatedUserId);
        if(!UserExists(userId) || !UserExists(relatedUserId)){
            log.info("UserId or relatedUserId does not exist");
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
        if(!UserExists(userId) || !UserExists(relatedUserId)){
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
        return userRepository.findByUserId(userId) != null;

    }
    private UUID getUserIdTest(){
        UserShortDto userShortDto = (UserShortDto) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return UUID.fromString(userShortDto.getUserId());
//        String userString = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        int startIndex = userString.indexOf("userId=");
//        int endIndex = userString.indexOf(",", startIndex);
//        return UUID.fromString((userString.substring(startIndex + 7, endIndex)));

    }

    private String getToken(String headerAuth){
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        throw new IllegalArgumentException("No Bearer token found");
    }
}

