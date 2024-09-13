package com.social.mc_friends.service;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    FriendShortDto confirmFriendRequest(String token, UUID uuid) throws UserException;

    FriendShortDto unblockFriend(String token, UUID uuid) throws UserException;

    FriendShortDto blockFriend(String token, UUID uuid) throws UserException;

    FriendShortDto createFriendRequest(String token, UUID uuid) throws UserException;

    FriendShortDto subscribeToFriend(String token, UUID uuid) throws UserException;

    Page<FriendShortDto> getFriendList(String token, String id, String isDeleted,
                                     String statusCode, String idTo, String previousStatusCode, Integer page, Integer size);

    FriendShortDto getFriendshipNote(String token, UUID uuid);

    void deleteFriend(String token, UUID uuid);

    List<UUID> getUserIdList(String token, String status);

//    List<Relationship> getRecommendations(FriendSearchDto searchDto);
    List<FriendShortDto> getRecommendations(FriendSearchDto searchDto);

    List<UUID> getAllFriendsIdList(String token);

    List<UUID> getFriendsIdListByUserId(UUID userId);

    CountDto getFriendRequestCount(String token);

    List<StatusCode> getStatuses(List<UUID> ids);

    List<UUID> getFriendsWhoBlockedUser(String token);
}
