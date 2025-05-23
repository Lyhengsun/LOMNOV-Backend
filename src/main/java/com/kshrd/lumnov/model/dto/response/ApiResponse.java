package com.kshrd.lumnov.model.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

  private Boolean success;
  private String message;
  private HttpStatus status;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T payload;
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}
