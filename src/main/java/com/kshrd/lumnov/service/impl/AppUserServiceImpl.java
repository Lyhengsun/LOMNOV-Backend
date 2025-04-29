package com.kshrd.lumnov.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kshrd.lumnov.exception.InvalidException;
import com.kshrd.lumnov.exception.NotFoundException;
import com.kshrd.lumnov.mapper.AppUserMapper;
import com.kshrd.lumnov.model.dto.request.AppUserRequest;
import com.kshrd.lumnov.model.dto.response.AppUserResponse;
import com.kshrd.lumnov.model.entity.AppUser;
import com.kshrd.lumnov.repository.AppUserRepository;
import com.kshrd.lumnov.service.AppUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final AppUserMapper appUserMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails userDetails = appUserRepository.getUserByEmail(username);
    if (userDetails == null) {
      throw new NotFoundException("User does not exist");
    }
    return userDetails;
  }

  @Override
  public AppUserResponse registerUser(AppUserRequest request) {
    if (appUserRepository.getRoleById(request.getRoleId()).equals("ROLE_ADMIN")) {
      throw new InvalidException("Invalid Role");
    }
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    AppUser appUser = appUserRepository.registerUser(request);
    return appUserMapper.toAppUserResponse(appUser);
  }

  @Override
  public AppUserResponse getProfile() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getProfile'");
  }

  @Override
  public void removeProfile() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'removeProfile'");
  }
}