package com.kshrd.lumnov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kshrd.lumnov.jwt.JwtAuthEntryPoint;
import com.kshrd.lumnov.jwt.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthFilter jwtAuthFilter;
  private final JwtAuthEntryPoint jwtAuthEntryPoint;

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
    // .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/auths/**",
    // "/v3/api-docs/**",
    // "/swagger-ui/**",
    // "/swagger-ui.html",
    // "/api/v1/files/**").permitAll().anyRequest().authenticated())
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
    // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    // return http.build();

    http
        .cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(request -> request
            .requestMatchers("/api/v1/auths/**", "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html", "/api/v1/files/**", "/api/v1/**")
            .permitAll()
            .requestMatchers("/api/v1/test").authenticated()
            .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}