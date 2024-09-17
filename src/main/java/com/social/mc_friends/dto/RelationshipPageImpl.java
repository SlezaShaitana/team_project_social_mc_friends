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

public class RelationshipPageImpl extends PageImpl<Relationship> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RelationshipPageImpl(@JsonProperty("content") List<Relationship> content,
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

    public RelationshipPageImpl(List<Relationship> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RelationshipPageImpl(List<Relationship> content) {
        super(content);
    }
    public RelationshipPageImpl(){
        super(new ArrayList<>());
    }
}
