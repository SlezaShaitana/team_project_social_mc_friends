package com.social.mc_friends.dto;

import lombok.Data;

@Data
public class CountDto {
    private int count;
    public CountDto(Integer count){
        this.count = count;
    }
}
