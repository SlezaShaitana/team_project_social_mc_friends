package com.social.mc_friends.v1;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.service.impl.FriendServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class ApiController {
    private final FriendServiceImpl friendService;
    @PutMapping("/{id}/approve")
    public ResponseEntity<FriendShortDto> confirmFriendRequest(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.confirmFriendRequest(uuid));
    }
    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockFriend(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.unblockFriend(uuid));
    }
    @PutMapping("/block/{id}")
    public ResponseEntity<FriendShortDto>  blockFriend(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.blockFriend(uuid));
    }
    @PostMapping("/{id}/request")
    public ResponseEntity<FriendShortDto> createFriendRequest(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.createFriendRequest(uuid));
    }
    @PostMapping("/subscribe/{id}")
    public ResponseEntity<FriendShortDto> subscribeToFriend(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.subscribeToFriend(uuid));
    }
    @GetMapping
    public ResponseEntity<PageFriendShortDto> getFriendList(FriendSearchDto searchDto, Pageable page){
        friendService.getFriendList(searchDto, page);
        return ResponseEntity.ok(new PageFriendShortDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendShortDto> getFriendshipNote(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.getFriendshipNote(uuid));
    }
    @DeleteMapping("/{id")
    public ResponseEntity deleteFriend(@PathVariable("id") UUID uuid){
        friendService.deleteFriend(uuid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/{status}")
    public ResponseEntity<List<UUID>> getFriendsIdList(StatusCode status){
        return ResponseEntity.ok(friendService.getFriendsIdList(status));
    }
    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendShortDto>> getRecommendations(FriendSearchDto searchDto){
        return ResponseEntity.ok(friendService.getRecommendations(searchDto));
    }

    @GetMapping
    public ResponseEntity<List<UUID>> getAllFriendsIdList(){
        return ResponseEntity.ok(friendService.getAllFriendsIdList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<List<UUID>> getFriendsIdListByUserId(@PathVariable("id") UUID uuid){
        return ResponseEntity.ok(friendService.getFriendsIdListByUserId(uuid));
    }
    @GetMapping("/count")
    public ResponseEntity<Integer> getFriendRequestCount(){
        return ResponseEntity.ok(friendService.getFriendRequestCount());
    }
    @GetMapping("/check")
    public ResponseEntity<List<StatusCode>> getStatuses(List<UUID> ids){
        return ResponseEntity.ok(friendService.getStatuses(ids));
    }
    @GetMapping("/blockFriendId")
    public ResponseEntity<List<UUID>> getFriendsWhoBlockedUser(){
        return ResponseEntity.ok(friendService.getFriendsWhoBlockedUser());
    }








}
