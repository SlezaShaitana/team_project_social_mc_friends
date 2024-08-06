package com.social.mc_friends.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserShortDto {
    private String userId;
    private String email;
    private List<String> roles;

}
