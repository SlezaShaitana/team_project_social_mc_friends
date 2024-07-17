package com.social.mc_friends.dto;

import lombok.Data;

@Data
public class FriendShortDto {
private String id;
private boolean isDeleted;
private StatusCode statusCode;
private String friendId;
private StatusCode previousStatusCode;
private int rating;
}
