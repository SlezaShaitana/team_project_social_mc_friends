package com.social.mc_friends.v1;

import com.social.mc_friends.dto.CountDto;
import com.social.mc_friends.dto.FriendShortDto;
import com.social.mc_friends.dto.FriendShortDtoPageImpl;
import com.social.mc_friends.dto.StatusCode;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.service.impl.FriendServiceImpl;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {
    @Mock
    private FriendServiceImpl friendService;
    @InjectMocks
    private ApiController apiController;
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(apiController)
                .build();
    }
    @Test
    @DisplayName("Test confirm friend request")
    void confirmFriendRequestTest() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
        , UUID.fromString(id), StatusCode.FRIEND, StatusCode.NONE, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);

        String response = objectMapper.writeValueAsString(friendShortDto);
        when(friendService.confirmFriendRequest(authorization, UUID.fromString(id))).thenReturn(friendShortDto);
        mockMvc.perform(put("/api/v1/friends/{id}", id + "/approve")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
        verify(friendService, times(1)).confirmFriendRequest(authorization, UUID.fromString(id));

    }
    @Test
    @DisplayName("Test unblock friend")
    void unblockFriendTest() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.FRIEND, StatusCode.BLOCKED, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);

        String response = objectMapper.writeValueAsString(friendShortDto);
        when(friendService.unblockFriend(authorization, UUID.fromString(id))).thenReturn(friendShortDto);
        mockMvc.perform(put("/api/v1/friends/unblock/{id}", id)
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
        verify(friendService, times(1)).unblockFriend(authorization, UUID.fromString(id));

    }

    @Test
    @DisplayName("Test block friend")
    void blockFriendTest() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.BLOCKED, StatusCode.FRIEND, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);

        String response = objectMapper.writeValueAsString(friendShortDto);
        when(friendService.blockFriend(authorization, UUID.fromString(id))).thenReturn(friendShortDto);
        mockMvc.perform(put("/api/v1/friends/block/{id}", id)
                            .header("Authorization", authorization)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(response));
        verify(friendService, times(1)).blockFriend(authorization, UUID.fromString(id));

    }
    @Test
    @DisplayName("Test create friend request")
    void createFriendRequestTest() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.REQUEST_FROM, StatusCode.NONE, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);

        String response = objectMapper.writeValueAsString(friendShortDto);
        when(friendService.createFriendRequest(authorization, UUID.fromString(id))).thenReturn(friendShortDto);
        mockMvc.perform(post("/api/v1/friends/{id}", id + "/request")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
        verify(friendService, times(1)).createFriendRequest(authorization, UUID.fromString(id));
    }

    @Test
    @DisplayName("Test get friendship note")
    void getFriendShipNote() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.REQUEST_FROM, StatusCode.NONE, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);
        when(friendService.getFriendshipNote(authorization, UUID.fromString(id))).thenReturn(friendShortDto);
        mockMvc.perform(get("/api/v1/friends/{id}", id)
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(friendShortDto)));

    }
    @Test
    @DisplayName("Test delete friend")
    void deleteFriendTest() throws Exception {
        String authorization = "token";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        mockMvc.perform(delete("/api/v1/friends/{id}", id)
                .header("Authorization", authorization))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test get userId list")
    void getUserIdList() throws Exception {
        String authorization = "token";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        String status = "FRIEND";
        List<UUID> userIdList = new ArrayList<>();
        for (int i = 0; i <= 4; i++){
            userIdList.add(UUID.fromString(id));
        }
        when(friendService.getUserIdList(authorization, status)).thenReturn(userIdList);
        mockMvc.perform(get("/api/v1/friends/status")
                .header("Authorization", authorization)
                        .param("s", status)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userIdList)));
    }

    @Test
    @DisplayName("Tes get all friend id list")
    void getAllFriendsIdList() throws Exception {
        String authorization = "token";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        List<UUID> userIdList = new ArrayList<>();
        for (int i = 0; i <= 4; i++){
            userIdList.add(UUID.fromString(id));
        }
        when(friendService.getAllFriendsIdList(authorization)).thenReturn(userIdList);
        mockMvc.perform(get("/api/v1/friends/friendId")
                        .header("Authorization", authorization)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userIdList)));
    }

    @Test
    @DisplayName("Test get friends id List by userId ")
    void getFriendsIdListByUserIdTest() throws Exception {
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        List<UUID> userIdList = new ArrayList<>();
        for (int i = 0; i <= 4; i++){
            userIdList.add(UUID.fromString(id));
        }
        when(friendService.getFriendsIdListByUserId(UUID.fromString(id))).thenReturn(userIdList);
        mockMvc.perform(get("/api/v1/friends/friendId/{id}", id)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userIdList)));
    }

    @Test
    @DisplayName("get friend request count")
    void getFriendRequestCountTest() throws Exception {
        String authorization = "token";
        CountDto countDto = new CountDto(3);
        when(friendService.getFriendRequestCount(authorization)).thenReturn(countDto);
        mockMvc.perform(get("/api/v1/friends/count/")
                .header("Authorization", authorization)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(countDto)));

    }

    @Test
    @DisplayName("Test get friends w ho blocked user")
    void getFriendsWhoBlockedUserTest() throws Exception {
        String authorization = "token";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        List<UUID> userIdList = new ArrayList<>();
        for (int i = 0; i <= 4; i++){
            userIdList.add(UUID.fromString(id));
        }
        when(friendService.getFriendsWhoBlockedUser(authorization)).thenReturn(userIdList);
        mockMvc.perform(get("/api/v1/friends/blockFriendId")
                        .header("Authorization", authorization)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userIdList)));
    }

    @Test
    @DisplayName("Test recommendations")
    void getRecommendationsTest() throws Exception {
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.REQUEST_FROM, StatusCode.NONE, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);
        List<FriendShortDto> friendShortDtoList = new ArrayList<>();
        for (int i = 0; i<= 3; i++){
            friendShortDtoList.add(friendShortDto);
        }
        when(friendService.getRecommendations(null)).thenReturn(friendShortDtoList);
        mockMvc.perform(get("/api/v1/friends/recommendations")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(friendShortDtoList)));
    }

    @Test
    @DisplayName("Test get friendList")
    void getFriendListTest() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");
        String statusCode = "REQUEST_FROM";

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.REQUEST_FROM, StatusCode.NONE, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);
        List<FriendShortDto> freindList = new ArrayList<>();
        for(int i = 0; i <=2; i++){
            freindList.add(friendShortDto);
        }
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        Page<FriendShortDto> pageFriendList = new FriendShortDtoPageImpl(freindList, pageable, 3);
        when(friendService.getFriendList(authorization, null, null, statusCode, null, null, 1, null)).thenReturn(pageFriendList);

        mockMvc.perform(get("/api/v1/friends")
                .header("Authorization", authorization)
                .param("statusCode", statusCode)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageFriendList)));

    }


}
