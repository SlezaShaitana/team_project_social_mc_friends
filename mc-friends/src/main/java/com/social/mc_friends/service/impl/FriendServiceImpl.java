package com.social.mc_friends.service.impl;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.model.Friend;
import com.social.mc_friends.service.FriendService;

import java.util.List;
import java.util.UUID;

public class FriendServiceImpl implements FriendService {
    @Override
    public FriendShortDto confirmFriendRequest(UUID uuid) {
        return null;
    }

    @Override
    public FriendShortDto unblockFriend(UUID uuid) {
        return null;
    }

    @Override
    public FriendShortDto blockFriend(UUID uuid) {
        return null;
    }

    @Override
    public FriendShortDto createFriendRequest(UUID uuid) {
        return null;
    }

    @Override
    public FriendShortDto subscribeToFriend(UUID uuid) {
        return null;
    }

    @Override
    public List<Friend> getFriendList(FriendSearchDto searchDto, Pageable page) {
        return null;
    }

    @Override
    public FriendShortDto getFriendshipNote(UUID uuid) {
        return null;
    }

    @Override
    public void deleteFriend(UUID uuid) {

    }

    @Override
    public List<UUID> getFriendsIdList(StatusCode status) {
        return null;
    }

    @Override
    public List<FriendShortDto> getRecommendations(FriendSearchDto searchDto) {
        return null;
    }

    @Override
    public List<UUID> getAllFriendsIdList() {
        return null;
    }

    @Override
    public List<UUID> getFriendsIdListByUserId(UUID userId) {
        return null;
    }

    @Override
    public Integer getFriendRequestCount() {
        return null;
    }

    @Override
    public List<StatusCode> getStatuses(List<UUID> ids) {
        return null;
    }

    @Override
    public List<UUID> getFriendsWhoBlockedUser() {
        return null;
    }
}
