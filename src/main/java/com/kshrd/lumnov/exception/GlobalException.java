package com.kshrd.lumnov.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.kshrd.lumnov.model.dto.response.BaseResponse;

@RestControllerAdvice
public class GlobalException extends BaseResponse {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		Map<String, String> error = new HashMap<>();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			error.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return problemDetailResponseEntity(error);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ProblemDetail> handlerMethodValidationException(HandlerMethodValidationException e) {
		Map<String, String> errors = new HashMap<>();
		for (MessageSourceResolvable pathError : e.getAllErrors()) {
			for (String err : Objects.requireNonNull(pathError.getCodes())) {
				System.out.println(err);
				if (err.contains("Positive")) {
					if (err.contains("size")) {
						errors.put("size", pathError.getDefaultMessage());
						break;
					} else if (err.contains("page")) {
						errors.put("page", pathError.getDefaultMessage());
						break;
					} else {
						errors.put("PositiveId", pathError.getDefaultMessage());
					}
				}
				if (err.contains("Min")) {
					errors.put("MinId", pathError.getDefaultMessage());
				}
				if (err.contains("page")) {
					errors.put("page", pathError.getDefaultMessage());
				}
			}
		}
		return problemDetailResponseEntity(errors);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
		return problemDetailResponseEntityCustom(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(WrongInputException.class)
	public ResponseEntity<?> wrongInputException(WrongInputException e) {
		return problemDetailResponseEntityCustom(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(EmailAlreadyExistException.class)
	public ResponseEntity<?> handleEmailAlreadyExistException(EmailAlreadyExistException e) {
		return problemDetailResponseEntityCustom(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ExpireOTPCodeException.class)
	public ResponseEntity<?> handleExpireOTPCodeException(ExpireOTPCodeException e) {
		return problemDetailResponseEntityCustom(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailNotRegisterException.class)
	public ResponseEntity<?> handleEmailNotRegisterException(EmailNotRegisterException e) {
		return problemDetailResponseEntityCustom(e.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidException.class)
	public ResponseEntity<?> handleInvalidException(InvalidException e) {
		return problemDetailResponseEntityCustom(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
