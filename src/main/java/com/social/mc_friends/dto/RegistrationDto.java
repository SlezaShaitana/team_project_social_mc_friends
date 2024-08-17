package com.social.mc_friends.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegistrationDto {
    private UUID uuid;
    private boolean deleted;
    private String email;
    private String password1;
    private String password2;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    private String captchaSecret;
    private Role role = Role.USER;
    private LocalDate reg_date;



}
