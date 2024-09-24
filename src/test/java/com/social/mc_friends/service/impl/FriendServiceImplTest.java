package com.social.mc_friends.service.impl;
import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.kafka.KafkaProducer;
import com.social.mc_friends.mapper.Mapper;
import com.social.mc_friends.model.Operation;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import com.social.mc_friends.repository.OperationRepository;
import com.social.mc_friends.repository.RelationshipRepository;
import com.social.mc_friends.repository.UserRepository;
import com.social.mc_friends.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@Nested
@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {
    @MockBean
    private RelationshipRepository relationshipRepository;
    @MockBean
    private OperationRepository operationRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private Mapper mapper;
    @InjectMocks
    private FriendServiceImpl friendService;

    @BeforeEach
    void setUp(){

        relationshipRepository = Mockito.mock(RelationshipRepository.class);
        operationRepository = Mockito.mock(OperationRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        jwtUtils = Mockito.mock(JwtUtils.class);
        kafkaProducer = Mockito.mock(KafkaProducer.class);
        mapper = Mockito.mock(Mapper.class);
        friendService = new FriendServiceImpl(relationshipRepository, operationRepository,userRepository, jwtUtils, kafkaProducer, mapper);

    }
    @Test
    @DisplayName("Test confirmFriend request")
    public void confirmFriendRequestTes() throws UserException {
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        UUID operationId = UUID.randomUUID();
        User user = new User(userId, "u@e.mail", "uFirstName", "uLastName");
        User relatedUser = new User(relatedUserId, "r@e.mail", "rFirstName", "rLastName");

        Relationship relationship = new Relationship(UUID.randomUUID(), userId, relatedUserId,
                StatusCode.FRIEND, StatusCode.NONE, operationId, 0);

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.findByUserId(relatedUserId)).thenReturn(relatedUser);

        FriendShortDto result = friendService.confirmFriendRequest(authorization, relatedUserId);
        assertNotNull(result);
        assertEquals(relationship.getRelatedUserId(), UUID.fromString(result.getFriendId()));
        assertEquals(String.valueOf(relationship.getStatusCode()), result.getStatusCode());
        assertEquals(String.valueOf(relationship.getPreviousStatusCode()), result.getPreviousStatusCode());

        verify(jwtUtils, times(1)).getId(any());
        verify(userRepository, times(4)).findByUserId(any(UUID.class));
        verify(relationshipRepository, times(2)).save(any(Relationship.class));
        verify(operationRepository, times(1)).save(any(Operation.class));
        verify(kafkaProducer, times(1)).sendNotificationMessage(any(NotificationDto.class));
    }
    @Test
    @DisplayName("Test unblock friend")
    public void unblockFriendTest() throws UserException {
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        UUID operationId = UUID.randomUUID();

        Relationship relationship = new Relationship(UUID.randomUUID(), userId, relatedUserId,
                StatusCode.BLOCKED, StatusCode.FRIEND, operationId, 0);
        Relationship reverseRelationship = new Relationship(UUID.randomUUID(), relatedUserId, userId,
                StatusCode.NONE, StatusCode.FRIEND, operationId, 0);

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId)).thenReturn(relationship);
        when(relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId)).thenReturn(reverseRelationship);


        FriendShortDto result = friendService.unblockFriend(authorization, relatedUserId);
        assertNotNull(result);
        assertEquals(relationship.getRelatedUserId(), UUID.fromString(result.getFriendId()));
        assertEquals(String.valueOf(relationship.getStatusCode()), result.getStatusCode());
        assertEquals(result.getStatusCode(), String.valueOf(StatusCode.FRIEND));
        assertEquals(result.getPreviousStatusCode(), String.valueOf(StatusCode.BLOCKED));

        verify(jwtUtils, times(1)).getId(any());
        verify(relationshipRepository, times(2)).save(any(Relationship.class));
        verify(relationshipRepository, times(1)).findByUserIdAndRelatedUserId(userId, relatedUserId);
        verify(relationshipRepository, times(1)).findByUserIdAndRelatedUserId(relatedUserId, userId);

    }
    @Test
    @DisplayName("Test block friend")
    public void blockFriendTest() throws UserException {
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        UUID operationId = UUID.randomUUID();
        User user = new User(userId, "u@e.mail", "uFirstName", "uLastName");
        User relatedUser = new User(relatedUserId, "r@e.mail", "rFirstName", "rLastName");

        Relationship relationship = new Relationship(UUID.randomUUID(), userId, relatedUserId,
                StatusCode.FRIEND, StatusCode.NONE, operationId, 0);
        Relationship reverseRelationship = new Relationship(UUID.randomUUID(), relatedUserId, userId,
                StatusCode.FRIEND, StatusCode.NONE, operationId, 0);
        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId)).thenReturn(relationship);
        when(relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId)).thenReturn(reverseRelationship);
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.findByUserId(relatedUserId)).thenReturn(relatedUser);

        FriendShortDto result = friendService.blockFriend(authorization, relatedUserId);

        assertNotNull(result);
        assertEquals(relationship.getRelatedUserId(), UUID.fromString(result.getFriendId()));
        assertEquals(String.valueOf(relationship.getStatusCode()), result.getStatusCode());
        assertEquals(result.getStatusCode(), String.valueOf(StatusCode.BLOCKED));
        assertEquals(result.getPreviousStatusCode(), String.valueOf(StatusCode.FRIEND));

        verify(jwtUtils, times(1)).getId(any());
        verify(userRepository, times(4)).findByUserId(any(UUID.class));
        verify(relationshipRepository, times(2)).save(any(Relationship.class));
        verify(relationshipRepository, times(1)).findByUserIdAndRelatedUserId(userId, relatedUserId);
        verify(relationshipRepository, times(1)).findByUserIdAndRelatedUserId(relatedUserId, userId);

    }
    @Test
    @DisplayName("Test friend request")
    public void createFriendRequestTest() throws UserException {
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        User user = new User(userId, "u@e.mail", "uFirstName", "uLastName");
        User relatedUser = new User(relatedUserId, "r@e.mail", "rFirstName", "rLastName");

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId)).thenReturn(null);
        when(relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId)).thenReturn(null);
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.findByUserId(relatedUserId)).thenReturn(relatedUser);

        FriendShortDto result = friendService.createFriendRequest(authorization, relatedUserId);
        assertNotNull(result);
        assertEquals(result.getStatusCode(),String.valueOf(StatusCode.REQUEST_TO));
        assertEquals(result.getPreviousStatusCode(), String.valueOf(StatusCode.NONE));

        verify(jwtUtils, times(1)).getId(any());
        verify(userRepository, times(4)).findByUserId(any(UUID.class));
        verify(relationshipRepository, times(2)).save(any(Relationship.class));
        verify(operationRepository, times(1)).save(any(Operation.class));
        verify(kafkaProducer, times(1)).sendNotificationMessage(any(NotificationDto.class));
    }
    @Test
    @DisplayName("Test subscribe to friend")
    public void subscribeToFriendTest() throws UserException {
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        User user = new User(userId, "u@e.mail", "uFirstName", "uLastName");
        User relatedUser = new User(relatedUserId, "r@e.mail", "rFirstName", "rLastName");

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId)).thenReturn(null);
        when(relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId)).thenReturn(null);
        when(userRepository.findByUserId(userId)).thenReturn(user);
        when(userRepository.findByUserId(relatedUserId)).thenReturn(relatedUser);

        FriendShortDto result = friendService.subscribeToFriend(authorization, relatedUserId);
        assertNotNull(result);
        assertEquals(result.getStatusCode(), String.valueOf(StatusCode.WATCHING));
        assertEquals(result.getPreviousStatusCode(), String.valueOf(StatusCode.NONE));

        verify(jwtUtils, times(1)).getId(any());
        verify(userRepository, times(4)).findByUserId(any(UUID.class));
        verify(relationshipRepository, times(2)).save(any(Relationship.class));
        verify(operationRepository, times(1)).save(any(Operation.class));
    }

    @Test
    @DisplayName("Test get friendshipNote")
    public void getFriendShipNote(){
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        Relationship relationship = new Relationship(UUID.randomUUID(), userId, relatedUserId, StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0);
        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId)).thenReturn(relationship);

        FriendShortDto result = friendService.getFriendshipNote(authorization, relatedUserId);

        assertNotNull(result);
        assertEquals(result.getStatusCode(), String.valueOf(StatusCode.FRIEND));
        verify(relationshipRepository, times(1)).findByUserIdAndRelatedUserId(userId, relatedUserId);
    }

    @Test
    @DisplayName("Test delete friend")
    public void deleteFriendTest(){
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID relatedUserId = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
                UUID operationId = UUID.randomUUID();

        Relationship relationship = new Relationship(UUID.randomUUID(), userId, relatedUserId,
                StatusCode.FRIEND, StatusCode.NONE, operationId, 0);
        Relationship reverseRelationship = new Relationship(UUID.randomUUID(), relatedUserId, userId,
                StatusCode.FRIEND, StatusCode.NONE, operationId, 0);
        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndRelatedUserId(userId, relatedUserId)).thenReturn(relationship);
        when(relationshipRepository.findByUserIdAndRelatedUserId(relatedUserId, userId)).thenReturn(reverseRelationship);

        friendService.deleteFriend(authorization, relatedUserId);

        assertEquals(relationship.getStatusCode(), StatusCode.NONE);
        assertEquals(relationship.getPreviousStatusCode(), StatusCode.FRIEND);
        assertEquals(reverseRelationship.getStatusCode(), StatusCode.NONE);
        assertEquals(reverseRelationship.getPreviousStatusCode(), StatusCode.FRIEND);

        verify(relationshipRepository, times(1)).findByUserIdAndRelatedUserId(userId, relatedUserId);

    }
    @Test
    @DisplayName("Test get friendRequest count")
    public void getFriendRequestCountTest(){
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID userId1 = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        Relationship relationship = new Relationship(UUID.randomUUID(), userId, UUID.randomUUID(), StatusCode.REQUEST_FROM, StatusCode.NONE, UUID.randomUUID(), 0);
        Relationship relationship1 = new Relationship(UUID.randomUUID(), userId1, UUID.randomUUID(), StatusCode.REQUEST_FROM, StatusCode.NONE, UUID.randomUUID(), 0);
        Relationship relationship2 = new Relationship(UUID.randomUUID(), userId, UUID.randomUUID(), StatusCode.REQUEST_FROM, StatusCode.NONE, UUID.randomUUID(),0);

        List<Relationship> relationshipList = new ArrayList<>();
        relationshipList.add(relationship);
        relationshipList.add(relationship1);
        relationshipList.add(relationship2);

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByUserIdAndStatusCode(userId)).thenReturn(relationshipList);

        CountDto result = friendService.getFriendRequestCount(authorization);
        assertNotNull(result);
        assertEquals(new CountDto(3), result);
        verify(relationshipRepository, times(1)).findByUserIdAndStatusCode(userId);
    }

    @Test
    @DisplayName("Test get UserId list")
    public void getUserIdList(){
        String authorization = "Bearer mockJwtToken";
        String statusCode = "FRIEND";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID userId1 = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        Relationship relationship = new Relationship(UUID.randomUUID(), userId, UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0);
        Relationship relationship1 = new Relationship(UUID.randomUUID(), userId1, UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0);

        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(relationship.getUuid());
        uuidList.add(relationship1.getUuid());

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findByStatus(statusCode)).thenReturn(uuidList);

        List<UUID> result = friendService.getUserIdList(authorization, statusCode);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(relationshipRepository, times(1)).findByStatus(statusCode);
    }
    @Test
    @DisplayName("Test get UserId list by userId")
    public void getFriendIdListByUserIdTest(){
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        Relationship relationship = new Relationship(UUID.randomUUID(), userId, UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0);
        Relationship relationship1 = new Relationship(UUID.randomUUID(), userId, UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0);
        Relationship relationship2 = new Relationship(UUID.randomUUID(), userId, UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0);

        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(relationship.getUuid());
        uuidList.add(relationship1.getUuid());
        uuidList.add(relationship2.getUuid());
        when(relationshipRepository.findAllFriendsId(userId)).thenReturn(uuidList);

        List<UUID> result = friendService.getFriendsIdListByUserId(userId);
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(relationshipRepository, times(1)).findAllFriendsId(userId);
    }

    @Test
    @DisplayName("Test get friends who blocked user")
    public void getFriendsWhoBlockedUserTest(){
        String authorization = "Bearer mockJwtToken";
        UUID relatedUserId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        Relationship relationship = new Relationship(UUID.randomUUID(), UUID.randomUUID(), relatedUserId, StatusCode.BLOCKED, StatusCode.FRIEND, UUID.randomUUID(), 0);
        Relationship relationship1 = new Relationship(UUID.randomUUID(), UUID.randomUUID(), relatedUserId, StatusCode.BLOCKED, StatusCode.BLOCKED, UUID.randomUUID(), 0);
        Relationship relationship2 = new Relationship(UUID.randomUUID(), UUID.randomUUID(), relatedUserId, StatusCode.BLOCKED, StatusCode.BLOCKED, UUID.randomUUID(), 0);
        List<UUID> uuidList = new ArrayList<>();
        uuidList.add(relationship.getUuid());
        uuidList.add(relationship1.getUuid());
        uuidList.add(relationship2.getUuid());

        when(jwtUtils.getId(any())).thenReturn(relatedUserId.toString());
        when(relationshipRepository.findBlockingFriendsId(relatedUserId)).thenReturn(uuidList);

        List<UUID> result = friendService.getFriendsWhoBlockedUser(authorization);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(relationshipRepository, times(1)).findBlockingFriendsId(relatedUserId);

    }
    @Test
    @DisplayName("Test get recommendations")
    public void getRecommendationsTest(){
        FriendSearchDto searchDto = new FriendSearchDto();
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");
        UUID userId1 = UUID.fromString("7982a7ce-fb47-4532-b2c1-e8c01f8fa844");
        User user = new User(userId, "u@e.mail", "uFirstName", "uLastName");
        User user1 = new User(userId1, "r@e.mail", "rFirstName", "rLastName");

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user1);

        when(userRepository.findAll()).thenReturn(userList);
        List<FriendShortDto> result = friendService.getRecommendations(searchDto);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();

    }
    @Test
    @DisplayName("Test get friendList")
    public void getFriendListTest(){
        String authorization = "Bearer mockJwtToken";
        UUID userId = UUID.fromString("12feafdd-3f5d-486a-b15c-a58153eac15d");

        Integer page = 1;
        Integer size = 3;
        String id = null;
        String isDeleted = null;
        String  statusCode = "FRIEND";
        String idTo = null;
        String previousStatusCode = null;

        List<Relationship> friendList = new ArrayList<>();
        friendList.add(new Relationship(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0));
        friendList.add(new Relationship(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), StatusCode.FRIEND, StatusCode.NONE, UUID.randomUUID(), 0));

        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        Page<Relationship> pageFriendList = new RelationshipPageImpl(friendList, pageable, 2);

        when(jwtUtils.getId(any())).thenReturn(userId.toString());
        when(relationshipRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(pageFriendList);

        Page<FriendShortDto> result = friendService.getFriendList(authorization, id, isDeleted, statusCode, idTo, previousStatusCode, page, size);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(relationshipRepository, times(1)).findAll(any(Specification.class), eq(pageable));

        }


}
