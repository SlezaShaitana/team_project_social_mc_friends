package com.social.mc_friends.dto;

import lombok.Data;

@Data
public class FriendShortDto {
private String id;
private boolean isDeleted;
private String statusCode;
private String friendId;
private String previousStatusCode;
private int rating;
}
