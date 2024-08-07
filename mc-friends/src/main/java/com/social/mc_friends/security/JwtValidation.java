package com.social.mc_friends.security;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "JwtValidation", url = "http://localhost:8086")
public interface JwtValidation {
    @RequestMapping(method = RequestMethod.GET, value = "/validation")
    Boolean validateToken(@RequestParam("token") String token);
}
