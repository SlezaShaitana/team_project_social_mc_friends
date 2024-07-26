package com.social.mc_friends.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private UUID uuid;
    private String name;
}
