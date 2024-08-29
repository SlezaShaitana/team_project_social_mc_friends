package com.social.mc_friends.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_friends.dto.RegistrationDto;
import com.social.mc_friends.model.User;
import com.social.mc_friends.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = "registerTopic", groupId =  "${spring.kafka.kafkaMessageGroupId}", containerFactory = "kafkaMessageConcurrentKafkaListenerContainerFactory")
    public void listenToRegisterTop(RegistrationDto registrationDto) throws JsonProcessingException {
        log.info("Received message: " + registrationDto);
        try {
            UUID userId = registrationDto.getUuid();
            if (userRepository.findById(userId).isEmpty()){
                User user = new User(registrationDto.getUuid(), registrationDto.getEmail(), registrationDto.getFirstName(), registrationDto.getLastName());
                userRepository.save(user);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            log.error("Failed to  create user from Kafka message");
        }


    }


}


