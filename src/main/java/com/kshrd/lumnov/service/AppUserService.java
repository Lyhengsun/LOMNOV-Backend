package com.kshrd.lumnov.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kshrd.lumnov.model.dto.request.AppUserRequest;
import com.kshrd.lumnov.model.dto.response.AppUserResponse;

public interface AppUserService extends UserDetailsService {
  public AppUserResponse registerUser(AppUserRequest request);

  AppUserResponse getProfile();

  void removeProfile();

  public String reSendOTP(String email);

  AppUserResponse verifyOTP(String email, String otp, Boolean isOTPRegister);

  String resetPassword(String email, String otp, String newPassword);
}