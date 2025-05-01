package com.kshrd.lumnov.model.dto.request;

import java.time.LocalDate;

import com.kshrd.lumnov.model.enumeration.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequest {
  private String fullName;
  private Gender gender;
  private LocalDate dateOfBirth;
  private String occupation;
  private String phoneNumber;
  private String email;
  private String password;
  private String avatarUrl;
  private String emergencyContact;
  private String deviceToken;
  private Integer roleId;
}