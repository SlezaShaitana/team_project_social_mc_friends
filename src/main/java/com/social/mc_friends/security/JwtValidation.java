package com.social.mc_friends.security;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
@Component
@FeignClient(value = "JwtValidation", url = "http://localhost:8086/api/v1/auth")
public interface JwtValidation {
    @GetMapping("/check-validation")
    Boolean validateToken(@RequestParam("token") String token);
}
