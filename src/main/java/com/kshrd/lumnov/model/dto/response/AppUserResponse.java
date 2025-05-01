package com.kshrd.lumnov.model.dto.response;

import java.time.LocalDate;

import com.kshrd.lumnov.model.enumeration.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponse {
  private Integer appUserId;
  private String fullName;
  private Gender gender;
  private LocalDate dateOfBirth;
  private String occupation;
  private String phoneNumber;
  private String email;
  private Boolean isVerified;
  private String avatarUrl;
  private String emergencyContact;
  private String deviceToken;
}