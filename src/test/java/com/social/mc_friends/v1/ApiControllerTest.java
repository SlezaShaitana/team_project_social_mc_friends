package com.social.mc_friends.v1;

import com.social.mc_friends.dto.FriendShortDto;
import com.social.mc_friends.dto.StatusCode;
import com.social.mc_friends.mapper.Mapper;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.repository.OperationRepository;
import com.social.mc_friends.repository.RelationshipRepository;
import com.social.mc_friends.repository.UserRepository;
import com.social.mc_friends.security.JwtUtils;
import com.social.mc_friends.service.impl.FriendServiceImpl;
import jakarta.ws.rs.core.MediaType;
import liquibase.GlobalConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {
    @Mock
    private FriendServiceImpl friendService;
    @Mock
    private RelationshipRepository relationshipRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OperationRepository operationRepository;
    @Mock
    private Mapper mapper;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private ApiController apiController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(apiController)
                .build();
    }
    @Test
    void confirmFriendRequest() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
        , UUID.fromString(id), StatusCode.FRIEND, StatusCode.NONE, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);

//        given(friendService.confirmFriendRequest(authorization, UUID.fromString(id))).willReturn(relationship);

        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(friendShortDto);
        when(friendService.confirmFriendRequest(authorization, UUID.fromString(id))).thenReturn(friendShortDto);

        mockMvc.perform(put("/api/v1/friends/{id}", id + "/approve")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));

    }
    @Test
    void unblockFriend() throws Exception {
        String authorization = "token";
        UUID relationshipId = UUID.fromString("c933556b-46f6-4254-bed2-8a45cbec10c4");
        String userId = "7982a7ce-fb47-4532-b2c1-e8c01f8fa844";
        String id = "12feafdd-3f5d-486a-b15c-a58153eac15d";
        UUID statusChangeId = UUID.fromString("54ae1eff-b4c2-4b84-9a87-ffb5197f37e5");

        Relationship relationship = new Relationship(relationshipId, UUID.fromString(userId)
                , UUID.fromString(id), StatusCode.FRIEND, StatusCode.BLOCKED, statusChangeId, 0);
        FriendShortDto friendShortDto = new FriendShortDto(relationship);

        given(friendService.unblockFriend(authorization, UUID.fromString(id))).willReturn(relationship);

        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(friendShortDto);

        mockMvc.perform(put("/api/v1/friends/unblock/{id}", id)
                        .header("Authorization", authorization)
                        .content(response)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
//                .andExpect(MockMvcResultMatchers.content().json(response));

    }
}
