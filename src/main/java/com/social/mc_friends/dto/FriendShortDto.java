package com.social.mc_friends.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.mc_friends.model.Relationship;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendShortDto {
 @JsonProperty("id")
private String id;
 @JsonProperty("deleted")
private boolean isDeleted;
 @JsonProperty("statusCode")
private String statusCode;
 @JsonProperty("friendId")
private String friendId;
 @JsonProperty("previousStatusCode")
private String previousStatusCode;
 @JsonProperty("rating")
private int rating;

public FriendShortDto(UUID uuid, StatusCode friend, UUID randomUUID, UUID randomed, StatusCode none, int i){

}
public FriendShortDto(Relationship relationship){
    this.id = String.valueOf(relationship.getStatusChangeId());
    this.statusCode = String.valueOf(relationship.getStatusCode());
    this.friendId = String.valueOf(relationship.getRelatedUserId());
    this.rating = (relationship.getRating());
    this.previousStatusCode = String.valueOf(relationship.getPreviousStatusCode());
    if (relationship.getStatusCode() != StatusCode.NONE){
        this.isDeleted = false;
    }
}


}
