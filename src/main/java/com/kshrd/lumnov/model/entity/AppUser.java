package com.kshrd.lumnov.model.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kshrd.lumnov.model.enumeration.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUser implements UserDetails {
  private Integer appUserId;
  private String fullName;
  private Gender gender;
  private LocalDate dateOfBirth;
  private String occupation;
  private String phoneNumber;
  private String email;
  private String password;
  private Boolean isVerified;
  private String avatarUrl;
  private String emergencyContact;
  private String deviceToken;
  private String role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
    return authorities;
  }

  @Override
  public String getUsername() {
    return email;
  }

}