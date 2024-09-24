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
import org.springframework.util.StringUtils;
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

//
    @PutMapping("/{id}/approve")
    public ResponseEntity<FriendShortDto> confirmFriendRequest(@RequestHeader("Authorization") String headerAuth,
                                                               @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(friendService.confirmFriendRequest(headerAuth, uuid));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/unblock/{id}")
    public ResponseEntity<FriendShortDto> unblockFriend(@RequestHeader("Authorization") String headerAuth,
                                                        @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(friendService.unblockFriend(headerAuth, uuid));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/block/{id}")
    public ResponseEntity<FriendShortDto>  blockFriend(@RequestHeader("Authorization") String headerAuth,
                                                       @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(friendService.blockFriend(headerAuth, uuid));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{id}/request")
    public ResponseEntity<FriendShortDto> createFriendRequest(@RequestHeader("Authorization") String headerAuth,
                                                              @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(friendService.createFriendRequest(headerAuth, uuid));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping("/subscribe/{id}")
    public ResponseEntity<FriendShortDto> subscribeToFriend(@RequestHeader("Authorization") String headerAuth,
                                                            @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        try {
            return ResponseEntity.ok(friendService.subscribeToFriend(headerAuth, uuid));
        } catch (UserException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping
    public ResponseEntity<Page<FriendShortDto>> getFriendList(@RequestHeader("Authorization") String headerAuth,
                                              @RequestParam(name = "id", required = false) String id,
                                              @RequestParam(name = "isDeleted", required = false) String isDeleted,
                                              @RequestParam(name = "statusCode", required = false) String statusCode,
                                              @RequestParam(name = "idTo", required = false) String idTo,
                                              @RequestParam(name = "previousStatusCode", required = false) String previousStatusCode,
                                              @RequestParam(name = "size", required = false) Integer size,
                                              @RequestParam(name = "p", defaultValue = "1") Integer page ){
        if (page < 1){
            page = 1;
        }
        return ResponseEntity.ok(friendService.getFriendList(headerAuth, id, isDeleted, statusCode, idTo, previousStatusCode, page, size));

    }
    @GetMapping("/{id}")
    public ResponseEntity<FriendShortDto> getFriendshipNote(@RequestHeader("Authorization") String headerAuth,
                                                            @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(friendService.getFriendshipNote(headerAuth, uuid));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFriend(@RequestHeader("Authorization") String headerAuth,
                                       @PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        friendService.deleteFriend(headerAuth, uuid);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @GetMapping("/status")
    public ResponseEntity<List<UUID>> getUserIdList(@RequestHeader("Authorization") String headerAuth,
                                                    @RequestParam(name = "s") String status){
        return ResponseEntity.ok(friendService.getUserIdList(headerAuth, status));
    }
    @GetMapping("/friendId")
    public ResponseEntity<List<UUID>> getAllFriendsIdList(@RequestHeader("Authorization") String headerAuth){
        return ResponseEntity.ok(friendService.getAllFriendsIdList(headerAuth));
    }
    @GetMapping("/friendId/{id}")
    public ResponseEntity<List<UUID>> getFriendsIdListByUserId(@PathVariable("id") String id){
        UUID uuid = UUID.fromString(id);
        return ResponseEntity.ok(friendService.getFriendsIdListByUserId(uuid));
    }
    @GetMapping("/count")
    public ResponseEntity<CountDto> getFriendRequestCount(@RequestHeader("Authorization") String headerAuth){
        return ResponseEntity.ok(friendService.getFriendRequestCount(headerAuth));
    }
    @GetMapping("/check")
    public ResponseEntity<List<StatusCode>> getStatuses(List<UUID> ids){
        return null;
    }
    @GetMapping("/blockFriendId")
    public ResponseEntity<List<UUID>> getFriendsWhoBlockedUser(@RequestHeader("Authorization") String headerAuth){
        return ResponseEntity.ok(friendService.getFriendsWhoBlockedUser(headerAuth));
    }
    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendShortDto>> getRecommendations(@RequestBody(required = false) FriendSearchDto searchDto){
        return ResponseEntity.ok(friendService.getRecommendations(searchDto));
    }

}
