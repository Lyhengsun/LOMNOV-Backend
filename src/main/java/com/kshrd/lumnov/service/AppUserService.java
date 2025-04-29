package com.kshrd.lumnov.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kshrd.lumnov.model.dto.request.AppUserRequest;
import com.kshrd.lumnov.model.dto.response.AppUserResponse;

public interface AppUserService extends UserDetailsService {
  public AppUserResponse registerUser(AppUserRequest request);

  AppUserResponse getProfile();

  void removeProfile();
}