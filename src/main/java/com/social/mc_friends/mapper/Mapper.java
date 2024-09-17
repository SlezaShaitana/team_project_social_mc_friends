package com.social.mc_friends.mapper;
import com.social.mc_friends.dto.FriendShortDto;
import com.social.mc_friends.dto.StatusCode;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Mapper {
//        public UserShortDto mapToUserShortDto(User user){
//        UserShortDto userShortDto = new UserShortDto();
//        userShortDto.setId(String.valueOf(user.getUuid()));
//        userShortDto.setName(user.getName());
//        return userShortDto;
//    }
    public FriendShortDto mapToFriendShortDto(Relationship relationship){
        FriendShortDto friendShortDto = new FriendShortDto(UUID.randomUUID(), StatusCode.FRIEND, UUID.randomUUID(), UUID.randomUUID(), StatusCode.NONE, 0);
        friendShortDto.setId(String.valueOf(relationship.getStatusChangeId()));
//        friendShortDto.setId(String.valueOf(relationship.getUserId()));
        friendShortDto.setDeleted(false);
        friendShortDto.setStatusCode(String.valueOf(relationship.getStatusCode()));
        friendShortDto.setFriendId(String.valueOf(relationship.getRelatedUserId()));
        friendShortDto.setPreviousStatusCode(String.valueOf(relationship.getPreviousStatusCode()));
        friendShortDto.setRating(relationship.getRating());
        return friendShortDto;
    }
    public FriendShortDto mapToFriendShortDto(User user){
        FriendShortDto friendShortDto = new FriendShortDto(UUID.randomUUID(), StatusCode.FRIEND, UUID.randomUUID(), UUID.randomUUID(), StatusCode.NONE, 0);
        friendShortDto.setId(String.valueOf(UUID.randomUUID()));
//        friendShortDto.setId(String.valueOf(relationship.getUserId()));
        friendShortDto.setDeleted(false);
        friendShortDto.setStatusCode(String.valueOf(StatusCode.NONE));
        friendShortDto.setFriendId(String.valueOf(user.getUserId()));
        friendShortDto.setPreviousStatusCode(String.valueOf(StatusCode.NONE));
        friendShortDto.setRating(0);
        return friendShortDto;
    }

}
