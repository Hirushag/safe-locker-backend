package com.sliit.safelocker.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String endPoint;
    private String requestMethod;
    private int status;
    private String requestBody;
    private LocalDateTime accessOn;
    private String responseTime;
    private String response;
    private String channel;

}
