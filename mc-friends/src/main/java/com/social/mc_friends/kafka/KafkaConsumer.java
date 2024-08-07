package com.social.mc_friends.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class KafkaConsumer {
    @KafkaListener(topics = "REGISTER_TOP", groupId = "mc-friends")
    public void listenToRegisterTop(String message){
        log.info("Received message: " + message);
    }
}
