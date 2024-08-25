package com.social.mc_friends.kafka;

import com.social.mc_friends.dto.MessageForKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.kafka.kafkaMessageTopicFriendRequest}")
    private String kafkaMessageTopicFriendRequest;
    @Value("${spring.kafka.kafkaMessageTopicFriendConfirmation}")
    private String kafkaMessageTopicFriendConfirmation;

    public void sendMessageForFriendRequest(MessageForKafkaDto friendRequestDto){
        kafkaTemplate.send(kafkaMessageTopicFriendRequest, friendRequestDto);
        log.info("Send message for friend request");
    }

    public void sendMessageForFriendConformation(MessageForKafkaDto friendConfirmationDto){
        kafkaTemplate.send(kafkaMessageTopicFriendConfirmation, friendConfirmationDto);
        log.info("Send message for friend confirmation");
    }

}
