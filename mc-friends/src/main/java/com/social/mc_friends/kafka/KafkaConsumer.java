package com.social.mc_friends.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_friends.model.User;
import com.social.mc_friends.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final UserRepository userRepository;
    @KafkaListener(topics = "registerTopic", groupId = "Userinfo")
    public void listenToRegisterTop(String message) throws JsonProcessingException {
        log.info("Received message: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        UUID userId = objectMapper.readValue(message, User.class).getUserId();
        if(!userRepository.findById(userId).isPresent()){
            User user = objectMapper.readValue(message, User.class);
            userRepository.save(user);
        }
    }
}
