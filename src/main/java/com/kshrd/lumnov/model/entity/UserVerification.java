package com.kshrd.lumnov.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVerification {
    private LocalDateTime expireDateTime;
    private String verification;
    private Integer userId;
}
