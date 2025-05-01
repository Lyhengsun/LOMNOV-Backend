package com.kshrd.lumnov.controller;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
  @Operation( summary = "Login")
  public ResponseEntity<?> Login(@Valid @RequestBody AuthRequest request)
      throws Exception {
    final UserDetails userDetails = appUserService.loadUserByUsername(request.getEmail());
    authenticate(userDetails.getUsername(), request.getPassword());
    final String token = jwtService.generateToken(userDetails);
    AuthResponse authResponse = new AuthResponse(token);
    ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
        .success(true)
        .message("Login Successfully")
        .payload(authResponse)
        .status(HttpStatus.OK)
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  @Operation( summary = "Register New User")
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

  @PostMapping("/verify-register")
  @Operation( summary = "Verify register email with OTP")
  public ResponseEntity<?> verifyOTPRegister(@RequestParam String email, @RequestParam String otp) {
    AppUserResponse appUserResponse = appUserService.verifyOTP(email, otp, true);

    ApiResponse<AppUserResponse> response = ApiResponse.<AppUserResponse>builder()
            .success(true)
            .message("Verify OTP Register successfully")
            .payload(appUserResponse)
            .status(HttpStatus.CREATED)
            .timestamp(LocalDateTime.now())
            .build();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/resend")
  @Operation( summary = "Resend Verified OTP")
  public ResponseEntity<?> resentOTP(@RequestParam String email){
    String resent = appUserService.reSendOTP(email);
    ApiResponse<AppUserResponse> response = ApiResponse.<AppUserResponse>builder()
            .success(true)
            .message(resent)
            .status(HttpStatus.OK)
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/verify-resetPassword")
  @Operation( summary = "Verify resetPassword email with OTP")
  public ResponseEntity<?> verifyOTPResetPassword(@RequestParam String email, @RequestParam String otp) {
    AppUserResponse appUserResponse = appUserService.verifyOTP(email, otp, false);

    ApiResponse<AppUserResponse> response = ApiResponse.<AppUserResponse>builder()
            .success(true)
            .message("Verify OTP Reset Password successfully")
            .payload(appUserResponse)
            .status(HttpStatus.CREATED)
            .timestamp(LocalDateTime.now())
            .build();

    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/resetPassword")
  @Operation( summary = "Reset Password")
  public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
    String resultMessage = appUserService.resetPassword(email, otp, newPassword);

    ApiResponse<String> response = ApiResponse.<String>builder()
            .success(true)
            .message(resultMessage)
            .status(HttpStatus.OK)
            .timestamp(LocalDateTime.now())
            .build();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
