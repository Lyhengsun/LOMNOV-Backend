package com.kshrd.lumnov.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kshrd.lumnov.jwt.JwtService;
import com.kshrd.lumnov.model.dto.request.AppUserRequest;
import com.kshrd.lumnov.model.dto.request.AuthRequest;
import com.kshrd.lumnov.model.dto.response.ApiResponse;
import com.kshrd.lumnov.model.dto.response.AppUserResponse;
import com.kshrd.lumnov.model.dto.response.AuthResponse;
import com.kshrd.lumnov.service.AppUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController {
  private final AppUserService appUserService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  private void authenticate(String email, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("Invalid username, email, or password. Please check your credentials and try again.",
          e);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequest request)
      throws Exception {
    final UserDetails userDetails = appUserService.loadUserByUsername(request.getEmail());
    authenticate(userDetails.getUsername(), request.getPassword());
    final String token = jwtService.generateToken(userDetails);
    AuthResponse authResponse = new AuthResponse(token);
    ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
        .success(true)
        .message("Authenticated Successfully")
        .payload(authResponse)
        .status(HttpStatus.OK)
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody AppUserRequest request) {
    AppUserResponse appUserResponse = appUserService.registerUser(request);
    ApiResponse<AppUserResponse> response = ApiResponse.<AppUserResponse>builder()
        .success(true)
        .message("Register user successfully")
        .payload(appUserResponse)
        .status(HttpStatus.CREATED)
        .timestamp(LocalDateTime.now())
        .build();
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
