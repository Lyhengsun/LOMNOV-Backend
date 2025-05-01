package com.kshrd.lumnov.service.impl;

import com.kshrd.lumnov.exception.EmailAlreadyExistException;
import com.kshrd.lumnov.exception.ExpireOTPCodeException;
import com.kshrd.lumnov.model.entity.UserVerification;
import com.kshrd.lumnov.repository.UserVerificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMapper appUserMapper;
    private final UserVerificationRepository userVerificationRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = appUserRepository.getUserByEmail(username);
        if (userDetails == null) {
            throw new NotFoundException("User does not exist");
        }
        return userDetails;
    }

    @SneakyThrows
    @Override
    public AppUserResponse registerUser(AppUserRequest request) {

        // validate in case role not exist
        if (appUserRepository.getRoleById(request.getRoleId()) == null) {
            throw new NotFoundException("Role ID '" + request.getRoleId() + "' does not exist.");
        }

        // validate in case choose role admin, not allow to register
        if (appUserRepository.getRoleById(request.getRoleId()).equals("ROLE_ADMIN")) {
            throw new InvalidException("Registration with the 'ROLE_ADMIN' role is not allowed.");
        }

        // validate email already exist
        if (appUserRepository.getUserByEmail(request.getEmail()) != null) {
            throw new EmailAlreadyExistException("The email address '" + request.getEmail() + "' is already in use.");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        AppUser appUser = appUserRepository.registerUser(request);

        // Prepare Email
        var randomNumber = new Random().nextInt(99999);
        UserVerification userVerification = new UserVerification();
        userVerification.setExpireDateTime(LocalDateTime.now().plusMinutes(5));
        userVerification.setVerification(String.format("%06d", randomNumber));
        userVerification.setUserId(appUser.getAppUserId());

        // Insert Into EmailVerification
        userVerificationRepository.insertUserVerify(userVerification);

        // Prepare Send Email To User
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        // Thymeleaf context setup
        Context context = new Context();
        context.setVariable("otp", userVerification.getVerification());

        // Process the template
        String htmlContent = templateEngine.process("otp-form", context);

        mimeMessageHelper.setSubject("Email Verify Company name");
        mimeMessageHelper.setTo(request.getEmail());
        mimeMessageHelper.setFrom(adminEmail);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);

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

    @Override
    public AppUserResponse verifyOTP(String email, String otp, Boolean isOTPRegister) {
        AppUser emailUser = appUserRepository.getUserByEmail(email);

        // validate email not yet register
        if (emailUser == null) {
            throw new NotFoundException("This email address is not registered.");
        }

        UserVerification userVerification = userVerificationRepository.getUserVerifyById(emailUser.getAppUserId());

        // validate user verify is null
        if (userVerification == null) {
            throw new ExpireOTPCodeException("The user verify is not found.");
        }

        // validate expire OTP code
        if (LocalDateTime.now().isAfter(userVerification.getExpireDateTime())) {
            throw new ExpireOTPCodeException("The OTP code has expired.");
        }

        // validate OTP mismatch
        if (!userVerification.getVerification().equals(otp)) {
            throw new InvalidException("Invalid OTP.");
        }

        // Check if the OTP is tied to the correct user
        if (!emailUser.getAppUserId().equals(userVerification.getUserId())) {
            throw new InvalidException("User mismatch.");
        }

        // Update user to verified
        AppUser verifiedUser = appUserRepository.updateUserToVerify(emailUser.getAppUserId(), true);

        // Delete OTP
        if(isOTPRegister) {
            userVerificationRepository.deleteOTPCodeById(userVerification.getUserId());
        }

        return appUserMapper.toAppUserResponse(verifiedUser);
    }

    @SneakyThrows
    @Override
    public String reSendOTP(String email) {
        AppUser emailUser = appUserRepository.getUserByEmail(email);

        // validate email not yet register
        if(emailUser == null) {
            throw new NotFoundException("This email address is not registered.");
        }

        UserVerification userVerification = userVerificationRepository.getUserVerifyById(emailUser.getAppUserId());

        if (userVerification != null) {
            // delete OTP
            userVerificationRepository.deleteOTPCodeById(emailUser.getAppUserId());
        }

        // Prepare Email
        var randomNumber = new Random().nextInt(99999);
        UserVerification newUserVerification = new UserVerification();
        newUserVerification.setExpireDateTime(LocalDateTime.now().plusMinutes(5));
        newUserVerification.setVerification(String.format("%06d", randomNumber));
        newUserVerification.setUserId(emailUser.getAppUserId());

        // Insert Into EmailVerification
        userVerificationRepository.insertUserVerify(newUserVerification);

        // Prepare Send Email To User
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        // Thymeleaf context setup
        Context context = new Context();
        context.setVariable("otp", newUserVerification.getVerification());

        // Process the template
        String htmlContent = templateEngine.process("otp-form", context);

        mimeMessageHelper.setSubject("Email Verify Company name");
        mimeMessageHelper.setTo(emailUser.getEmail());
        mimeMessageHelper.setFrom(adminEmail);
        mimeMessageHelper.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);

        return "OTP resent successfully";
    }

    @SneakyThrows
    @Override
    public String resetPassword(String email, String otp, String newPassword) {
        AppUser emailUser = appUserRepository.getUserByEmail(email);

        // validate email not yet register
        if(emailUser == null) {
            throw new NotFoundException("This email address is not registered.");
        }

        UserVerification userVerification = userVerificationRepository.getUserVerifyById(emailUser.getAppUserId());

        // validate user verify is null
        if (userVerification == null) {
            throw new ExpireOTPCodeException("The user verify is not found.");
        }

        // validate expire OTP code
        if (LocalDateTime.now().isAfter(userVerification.getExpireDateTime())) {
            throw new ExpireOTPCodeException("The OTP code has expired.");
        }

        // validate OTP mismatch
        if (!userVerification.getVerification().equals(otp)) {
            throw new InvalidException("Invalid OTP.");
        }

        // Check if the OTP is tied to the correct user
        if (!emailUser.getAppUserId().equals(userVerification.getUserId())) {
            throw new InvalidException("User mismatch.");
        }

        // update user with new password
        System.out.println("newPassword: " + newPassword);
        String newPasswordEncode = passwordEncoder.encode(newPassword);
        appUserRepository.updatePasswordByEmail(email, newPasswordEncode);

        // delete from table OTP
        userVerificationRepository.deleteOTPCodeById(userVerification.getUserId());

        return "Password has been reset successfully.";
    }
}