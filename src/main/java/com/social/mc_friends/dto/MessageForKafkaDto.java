package com.social.mc_friends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageForKafkaDto {
    private UUID requestFromId;
    private UUID requestToId;
}
