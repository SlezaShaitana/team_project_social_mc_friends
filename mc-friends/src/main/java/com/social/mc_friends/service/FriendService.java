package com.social.mc_friends.service;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FriendService {
    Relationship confirmFriendRequest(UUID uuid) throws UserException;
    Relationship unblockFriend(UUID uuid) throws UserException;
    Relationship blockFriend(UUID uuid) throws UserException;
    Relationship createFriendRequest(UUID uuid) throws UserException;
    Relationship subscribeToFriend(UUID uuid) throws UserException;
    Page<Relationship> getFriendList(FriendSearchDto searchDto, Integer page);
    Relationship getFriendshipNote(UUID uuid);
    void deleteFriend(UUID uuid);
    List<UUID> getUserIdList(String status);
    List<Relationship> getRecommendations(FriendSearchDto searchDto);
    List<UUID> getAllFriendsIdList();
    List<UUID> getFriendsIdListByUserId(UUID userId);
    Integer getFriendRequestCount();
    List<StatusCode> getStatuses(List<UUID> ids);
    List<UUID> getFriendsWhoBlockedUser();









}
