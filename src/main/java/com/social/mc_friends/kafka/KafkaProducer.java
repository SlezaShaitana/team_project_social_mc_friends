package com.social.mc_friends.kafka;

import com.social.mc_friends.dto.NotificationDto;
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
    @Value("${spring.kafka.kafkaNotificationTopic}")
    private String kafkaNotificationTopic;


    public void sendNotificationMessage(NotificationDto notificationDto){
        kafkaTemplate.send(kafkaNotificationTopic, notificationDto);
        log.info("Send message for friend request");
    }



}
