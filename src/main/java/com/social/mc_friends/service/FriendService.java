package com.social.mc_friends.service;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    Relationship confirmFriendRequest(String token, UUID uuid) throws UserException;

    Relationship unblockFriend(String token, UUID uuid) throws UserException;

    Relationship blockFriend(String token, UUID uuid) throws UserException;

    Relationship createFriendRequest(String token, UUID uuid) throws UserException;

    Relationship subscribeToFriend(String token, UUID uuid) throws UserException;

    Page<Relationship> getFriendList(String token, String id, String isDeleted, String idFrom,
                                     String statusCode, String idTo, String previousStatusCode, Integer page, Integer size);

    Relationship getFriendshipNote(String token, UUID uuid);

    void deleteFriend(String token, UUID uuid);

    List<UUID> getUserIdList(String token, String status);

//    List<Relationship> getRecommendations(FriendSearchDto searchDto);
    List<User> getRecommendations(FriendSearchDto searchDto);

    List<UUID> getAllFriendsIdList(String token);

    List<UUID> getFriendsIdListByUserId(UUID userId);

    Integer getFriendRequestCount(String token);

    List<StatusCode> getStatuses(List<UUID> ids);

    List<UUID> getFriendsWhoBlockedUser(String token);
}
