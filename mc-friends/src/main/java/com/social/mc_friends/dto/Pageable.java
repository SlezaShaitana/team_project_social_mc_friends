package com.social.mc_friends.dto;

import lombok.Data;

@Data
public class Pageable {
    private int page;
    private int size;
    private Sort sort;
}
