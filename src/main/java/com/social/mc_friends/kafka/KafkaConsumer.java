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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final UserRepository userRepository;
    @KafkaListener(topics = "registerTopic", groupId = "Userinfo")
    public void listenToRegisterTop(String message) throws JsonProcessingException {
        log.info("Received message: " + message);
        User user = new User();
        String regexId = "uuid=[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";
        String regexEmail = "/^[A-Z0-9._%+-]+@[A-Z0-9-]+.+.[A-Z]{2,4}$/i";
        user.setUserId(UUID.fromString(getExpression(message, regexId)));
        user.setEmail(getExpression(message, regexEmail));
        }

    private String getExpression(String text, String regex) {
        String expression = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            expression = matcher.group(1);
    }
        return expression;
    }
}


