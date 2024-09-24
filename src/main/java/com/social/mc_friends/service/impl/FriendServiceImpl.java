package com.social.mc_friends.service.impl;


import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.kafka.KafkaProducer;
import com.social.mc_friends.mapper.Mapper;
import com.social.mc_friends.model.*;
import com.social.mc_friends.repository.*;
import com.social.mc_friends.repository.specificftions.FriendsSpecifications;
import com.social.mc_friends.security.JwtUtils;
import com.social.mc_friends.service.FriendService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.social.mc_friends.dto.StatusCode.REQUEST_FROM;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final RelationshipRepository relationshipRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final KafkaProducer kafkaProducer;
    private final Mapper mapper;


    @Override
    @Transactional
    public FriendShortDto confirmFriendRequest(String token, UUID relatedUserId) throws UserException{
            log.info("confirmFriendRequest execution started");
            UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
            Operation operation = createOperation(userId, relatedUserId, OperationType.FRIENDSHIP_CONFIRMATION);
            Relationship relationship = makeRelationship(userId,  relatedUserId, StatusCode.FRIEND, operation);
            saveReverseRelationship(relatedUserId, userId, StatusCode.FRIEND, operation);
            NotificationDto notificationDto = makeNotificationDto(userId, relatedUserId, operation);
            notificationDto.setNotificationType(NotificationType.FRIEND_REQUEST_CONFIRMATION);
            notificationDto.setContent(" добавил Вас в друзья");
            kafkaProducer.sendNotificationMessage(notificationDto);
            return new FriendShortDto(relationship);
    }

    @Override
    @Transactional
    public FriendShortDto unblockFriend(String token, UUID relatedUserId) throws UserException {
            log.info("unblockFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
            Operation operation = createOperation(userId, relatedUserId, OperationType.UNBLOCKING);
            Relationship relationship = restoreStatus(userId, relatedUserId, operation);
            Relationship reverseRelationship = restoreStatus(relatedUserId, userId, operation);
            return new FriendShortDto(relationship);
    }

    @Override
    public FriendShortDto blockFriend(String token, UUID relatedUserId) throws UserException {
        log.info("blockFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
            Operation operation = createOperation(userId, relatedUserId, OperationType.BLOCKING);
            Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.BLOCKED, operation);
            Relationship reverseRelationship = makeRelationship(relatedUserId, userId, StatusCode.NONE, operation);
            return new FriendShortDto(relationship);
    }

    @Override
    public FriendShortDto createFriendRequest(String token, UUID relatedUserId) throws UserException {
        log.info("createFriendRequest execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Operation operation = createOperation(userId, relatedUserId, OperationType.FRIEND_REQUEST);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.REQUEST_TO, operation);
        saveReverseRelationship(relatedUserId, userId, REQUEST_FROM, operation);
        NotificationDto notificationDto = makeNotificationDto(userId, relatedUserId, operation);
        notificationDto.setNotificationType(NotificationType.FRIEND_REQUEST);
        notificationDto.setContent("Вам отправлен запрос на добавление в друзья от ");
        kafkaProducer.sendNotificationMessage(notificationDto);
        return new FriendShortDto(relationship);
    }

    @Override
    public FriendShortDto subscribeToFriend(String token, UUID relatedUserId) throws UserException {
        log.info("subscribeToFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Operation operation = createOperation(userId, relatedUserId, OperationType.SUBSCRIPTION);
        Relationship relationship = makeRelationship(userId, relatedUserId, StatusCode.WATCHING, operation);
        saveReverseRelationship(relatedUserId, userId, StatusCode.SUBSCRIBED, operation);
        return new FriendShortDto(relationship);
    }


    @Override
    public Page<FriendShortDto> getFriendList(String token, String id, String isDeleted,
                                            String statusCode, String idTo, String previousStatusCode, Integer page, Integer size) {
        log.info("getFriendList execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Specification<Relationship> spec = Specification.where(null);
        log.info("userId = " + userId);
        if (token != null){
            spec = spec.and(FriendsSpecifications.userIdEquals(userId));
        }
        if (id != null){
            spec = spec.and(FriendsSpecifications.operationIdEquals(UUID.fromString(id)));
        }

        if (idTo != null){
            spec = spec.and(FriendsSpecifications.friendIdToEquals(UUID.fromString(idTo)));
        }
        if (statusCode != null){
            spec = spec.and(FriendsSpecifications.friendsStatusEquals(StatusCode.valueOf(statusCode)));
        }

        if (previousStatusCode != null){
            spec = spec.and(FriendsSpecifications.friendsPreviousStatusEquals(StatusCode.valueOf(previousStatusCode)));
        }
        return relationshipRepository.findAll(spec, PageRequest.of(page - 1, size)).map(FriendShortDto::new);

    }

    @Override
    public FriendShortDto getFriendshipNote(String token, UUID relatedUserId) {
        log.info("getFriendshipNote execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        return new FriendShortDto(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId));
    }

    @Override
    public void deleteFriend(String token, UUID relatedUserId) {
        log.info("deleteFriend execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
        Operation operation = createOperation(userId, relatedUserId, OperationType.REMOVAL);
        Relationship relationship = relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
        StatusCode previousStatusCode = relationship.getStatusCode();
        if (previousStatusCode.equals(StatusCode.REQUEST_FROM)) {
            relationship.setStatusCode(StatusCode.DECLINED);
        } else {
            relationship.setStatusCode(StatusCode.NONE);
        }
        relationship.setPreviousStatusCode(previousStatusCode);
        relationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(relationship);
        Relationship reverseRelationship = relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId);
        StatusCode reversePreviousStatusCode = reverseRelationship.getStatusCode();
        if (previousStatusCode.equals(StatusCode.REQUEST_TO)) {
            reverseRelationship.setStatusCode(StatusCode.REJECTING);
        } else {
            reverseRelationship.setStatusCode(StatusCode.NONE);
        }
        reverseRelationship.setPreviousStatusCode(reversePreviousStatusCode);
        reverseRelationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(reverseRelationship);

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
    public CountDto getFriendRequestCount(String token) {
        log.info("getFriendRequestCount execution started");
        UUID userId = UUID.fromString(jwtUtils.getId(getToken(token)));
       List<Relationship> relationshipList = relationshipRepository.findByUserIdAndStatusCode(userId);
        return new CountDto(relationshipList.size());
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

    @Override
    public List<FriendShortDto> getRecommendations(FriendSearchDto searchDto) {
        log.info("getRecommendations execution started");
        log.info("Check for update");
        return userRepository.findAll().stream().map(mapper::mapToFriendShortDto)  .collect(Collectors.toList());
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

    private String getToken(String headerAuth){
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        throw new IllegalArgumentException("No Bearer token found");
    }
    private NotificationDto makeNotificationDto(UUID userId, UUID relatedUserId, Operation operation){
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setAuthorId(userId);
        notificationDto.setSentTime(LocalDateTime.now());
        notificationDto.setReceiverId(relatedUserId);
        notificationDto.setServiceName(MicroServiceName.FRIENDS);
        notificationDto.setEventId(operation.getUuid());
        notificationDto.setUuid(null);
        notificationDto.setIsReaded(null);
        return notificationDto;
    }
    private Relationship restoreStatus(UUID userId, UUID relatedUserId, Operation operation){
        Relationship relationship = relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId);
        StatusCode statusCode = relationship.getPreviousStatusCode();
        StatusCode previousStatusCode = relationship.getStatusCode();
        relationship.setStatusCode(statusCode);
        relationship.setPreviousStatusCode(previousStatusCode);
        relationship.setStatusChangeId(operation.getUuid());
        relationshipRepository.save(relationship);
        return relationship;
    }
}

