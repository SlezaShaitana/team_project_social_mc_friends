package com.social.mc_friends.service;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.model.Friend;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    FriendShortDto confirmFriendRequest(UUID uuid);
    FriendShortDto unblockFriend(UUID uuid);
    FriendShortDto blockFriend(UUID uuid);
    FriendShortDto createFriendRequest(UUID uuid);
    FriendShortDto subscribeToFriend(UUID uuid);
    List<Friend> getFriendList(FriendSearchDto searchDto, Pageable page);
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
