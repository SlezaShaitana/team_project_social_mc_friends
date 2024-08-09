package com.social.mc_friends.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_friends.dto.UserShortDto;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.util.Base64;
import java.util.List;

@Component
public class JwtUtils {
    public UserShortDto mapToUserShortDto (String token) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getPayLoad(token), UserShortDto.class);
    }

    private String getPayLoad(String token){
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return new String(decoder.decode(chunks[1]));
    }

}
