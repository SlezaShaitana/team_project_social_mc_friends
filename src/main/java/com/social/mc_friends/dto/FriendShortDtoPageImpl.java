package com.social.mc_friends.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.social.mc_friends.model.Relationship;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class FriendShortDtoPageImpl extends PageImpl<FriendShortDto> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public FriendShortDtoPageImpl(@JsonProperty("content") List<FriendShortDto> content,
                                @JsonProperty("number") int number,
                                @JsonProperty("size") int size,
                                @JsonProperty("totalElements") Long totalElements,
                                @JsonProperty("pageable") JsonNode pageable,
                                @JsonProperty("last") boolean last,
                                @JsonProperty("totalPages") int totalPages,
                                @JsonProperty("sort") JsonNode sort,
                                @JsonProperty("numberOfElements") int numberOfElements){
        super(content, PageRequest.of(
                pageable.get("page").asInt(),
                pageable.get("size").asInt()
        ), totalElements);

    }

    public FriendShortDtoPageImpl(List<FriendShortDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public FriendShortDtoPageImpl(List<FriendShortDto> content) {
        super(content);
    }
    public FriendShortDtoPageImpl(){
        super(new ArrayList<>());
    }
}
