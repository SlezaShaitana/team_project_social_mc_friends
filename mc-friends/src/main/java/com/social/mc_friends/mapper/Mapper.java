package com.social.mc_friends.mapper;
import com.social.mc_friends.dto.FriendShortDto;
import com.social.mc_friends.dto.UserShortDto;
import com.social.mc_friends.model.Relationship;
import com.social.mc_friends.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        FriendShortDto friendShortDto = new FriendShortDto();
        friendShortDto.setId(String.valueOf(relationship.getStatusChangeId()));
        friendShortDto.setId(String.valueOf(relationship.getUserId()));
        friendShortDto.setDeleted(false);
        friendShortDto.setStatusCode(String.valueOf(relationship.getStatusCode()));
        friendShortDto.setFriendId(String.valueOf(relationship.getRelatedUserId()));
        friendShortDto.setPreviousStatusCode(String.valueOf(relationship.getPreviousStatusCode()));
        friendShortDto.setRating(relationship.getRating());
        return friendShortDto;
    }

}
