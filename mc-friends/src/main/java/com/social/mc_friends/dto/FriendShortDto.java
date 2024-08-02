package com.social.mc_friends.dto;

import com.social.mc_friends.model.Relationship;
import lombok.Data;

@Data
public class FriendShortDto {
private String id;
private boolean isDeleted;
private String statusCode;
private String friendId;
private String previousStatusCode;
private int rating;

public FriendShortDto(){

}
public FriendShortDto(Relationship relationship){
    this.id = String.valueOf(relationship.getUuid());
    this.statusCode = String.valueOf(relationship.getStatusCode());
    this.friendId = String.valueOf(relationship.getRelatedUserId());
    this.rating = (relationship.getRating());
    this.previousStatusCode = String.valueOf(relationship.getPreviousStatusCode());
    if (relationship.getStatusCode() != StatusCode.NONE){
        this.isDeleted = false;
    }
}
}
