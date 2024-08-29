package com.social.mc_friends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private UUID uuid;
    private UUID authorId;
    private String content;
    private NotificationType notificationType;
    private LocalDateTime sentTime;
    private UUID receiverId;
    private MicroServiceName serviceName;
    private UUID eventId;
    private Boolean isReaded;
}
