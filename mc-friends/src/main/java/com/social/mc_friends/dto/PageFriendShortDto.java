package com.social.mc_friends.dto;

import lombok.Data;

@Data
public class PageFriendShortDto {
    private int totalElements;
    private int totalPages;
    private int number;
    private int size;
    private FriendShortDto[] content;
    private Sort sort;
    private boolean first;
    private boolean last;
    private int numberOfElements;
    private PageableObject pageable;
    private boolean empty;

}
