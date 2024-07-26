package com.social.mc_friends.v1;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.mapper.Mapper;
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
    private final Mapper mapper;
    @PutMapping("/{id}/approve")
    public ResponseEntity<FriendShortDto> confirmFriendRequest(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.confirmFriendRequest((uuid))));
    }
    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.unblockFriend(uuid)));
    }
    @PutMapping("/block/{id}")
    public ResponseEntity<FriendShortDto>  blockFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.blockFriend(uuid)));
    }
    @PostMapping("/{id}/request")
    public ResponseEntity<FriendShortDto> createFriendRequest(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.createFriendRequest(uuid)));
    }
    @PostMapping("/subscribe/{id}")
    public ResponseEntity<FriendShortDto> subscribeToFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.subscribeToFriend(uuid)));
    }
    @GetMapping
    public ResponseEntity<PageFriendShortDto> getFriendList(FriendSearchDto searchDto, Pageable page){
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendShortDto> getFriendshipNote(@PathVariable("id") UUID uuid){
        return null;
    }
    @DeleteMapping("/{id}")
    public void deleteFriend(@PathVariable("id") UUID uuid){

    }
    @GetMapping("/{status}")
    public ResponseEntity<List<UUID>> getFriendsIdList(StatusCode status){
       return null;
    }
    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendShortDto>> getRecommendations(FriendSearchDto searchDto){
        return null;
    }

    @GetMapping("/friendId")
    public ResponseEntity<List<UUID>> getAllFriendsIdList(){
        return null;
    }
    @GetMapping("/friendId/{id}")
    public ResponseEntity<List<UUID>> getFriendsIdListByUserId(@PathVariable("id") UUID uuid){
        return null;
    }
    @GetMapping("/count")
    public ResponseEntity<Integer> getFriendRequestCount(){
        return null;
    }
    @GetMapping("/check")
    public ResponseEntity<List<StatusCode>> getStatuses(List<UUID> ids){
        return null;
    }
    @GetMapping("/blockFriendId")
    public ResponseEntity<List<UUID>> getFriendsWhoBlockedUser(){
        return null;
    }

}
