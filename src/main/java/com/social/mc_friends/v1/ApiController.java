package com.social.mc_friends.v1;

import com.social.mc_friends.dto.*;
import com.social.mc_friends.exceptons.UserException;
import com.social.mc_friends.mapper.Mapper;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.security.JwtUtils;
import com.social.mc_friends.service.impl.FriendServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class ApiController {
    private final FriendServiceImpl friendService;
    private final Mapper mapper;
    private final JwtUtils jwtUtils;
    @PutMapping("/{id}/approve")
    public ResponseEntity<FriendShortDto> confirmFriendRequest(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.confirmFriendRequest((uuid))));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.unblockFriend(uuid)));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/block/{id}")
    public ResponseEntity<FriendShortDto>  blockFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.blockFriend(uuid)));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{id}/request")
    public ResponseEntity<FriendShortDto> createFriendRequest(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.createFriendRequest(uuid)));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/subscribe/{id}")
    public ResponseEntity<FriendShortDto> subscribeToFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.subscribeToFriend(uuid)));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping
    public Page<FriendShortDto> getFriendList(@RequestBody FriendSearchDto searchDto,
                              @RequestParam(name = "p", defaultValue = "1") Integer page ){
        if (page < 1){
            page = 1;
        }
        return friendService.getFriendList(searchDto, page).map(FriendShortDto::new);

    }
    @GetMapping("/{id}")
    public ResponseEntity<FriendShortDto> getFriendshipNote(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(mapper.mapToFriendShortDto(friendService.getFriendshipNote(uuid)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFriend(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        friendService.deleteFriend(uuid);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @GetMapping("/status")
    public ResponseEntity<List<UUID>> getUserIdList(@RequestParam(name = "s") String status){
        return ResponseEntity.ok(friendService.getUserIdList(status));
    }
    @GetMapping("/friendId")
    public ResponseEntity<List<UUID>> getAllFriendsIdList(){
        return ResponseEntity.ok(friendService.getAllFriendsIdList());
    }
    @GetMapping("/friendId/{id}")
    public ResponseEntity<List<UUID>> getFriendsIdListByUserId(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(friendService.getFriendsIdListByUserId(uuid));
    }
    @GetMapping("/count")
    public ResponseEntity<CountDto> getFriendRequestCount(){
        return ResponseEntity.ok(new CountDto(friendService.getFriendRequestCount()));
    }
    @GetMapping("/check")
    public ResponseEntity<List<StatusCode>> getStatuses(List<UUID> ids){
        return null;
    }
    @GetMapping("/blockFriendId")
    public ResponseEntity<List<UUID>> getFriendsWhoBlockedUser(){
        return ResponseEntity.ok(friendService.getFriendsWhoBlockedUser());
    }
    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendShortDto>> getRecommendations(@RequestBody FriendSearchDto searchDto){
        return ResponseEntity.ok(friendService.getRecommendations(searchDto).stream()
                .map(mapper::mapToFriendShortDto)
                .collect(Collectors.toList()));
    }

}
