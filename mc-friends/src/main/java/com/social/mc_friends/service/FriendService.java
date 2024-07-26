package com.social.mc_friends.service;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    Relationship confirmFriendRequest(UUID uuid);
    Relationship unblockFriend(UUID uuid);
    Relationship blockFriend(UUID uuid);
    Relationship createFriendRequest(UUID uuid);
    Relationship subscribeToFriend(UUID uuid);
    List<User> getFriendList(FriendSearchDto searchDto, Pageable page);
    FriendShortDto getFriendshipNote(UUID uuid);
    void deleteFriend(UUID uuid);
    List<UUID> getFriendsIdList(StatusCode status);
    List<FriendShortDto> getRecommendations(FriendSearchDto searchDto);
    List<UUID> getAllFriendsIdList();
    List<UUID> getFriendsIdListByUserId(UUID userId);
    Integer getFriendRequestCount();
    List<StatusCode> getStatuses(List<UUID> ids);
    List<UUID> getFriendsWhoBlockedUser();









}
