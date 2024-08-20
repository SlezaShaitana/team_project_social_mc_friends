package com.social.mc_friends.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class FriendSearchDto {
private String id;
private boolean isDeleted;
private String idFrom;
private String statusCode;
private String idTo;
private String previousStatusCode;

}