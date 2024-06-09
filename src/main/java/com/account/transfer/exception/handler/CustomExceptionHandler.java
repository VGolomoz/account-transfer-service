package com.account.transfer.exception.handler;

import com.account.transfer.api.dto.ErrorResponse;
import com.account.transfer.exception.ErrorMessage;
import com.account.transfer.exception.ServiceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.account.transfer.exception.AppErrorCode.FIELDS_VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ErrorResponse> handleFunException(ServiceException ex) {
        var response = ResponseEntity
                .status(ex.getErrorStatus())
                .body(buildErrorDto(ex, ex.getErrorStatus().value() + ex.getErrorCode().getCode()));
        return response;

    }

    private ErrorResponse buildErrorDto(ServiceException ex, String internalErrorCode) {
        return ErrorResponse.builder()
                .errorCode(internalErrorCode)
                .errorMessage(ErrorMessage.builder()
                        .key(ex.getErrorCode().name())
                        .text(ex.getMessage())
                        .build())
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var responseBody = ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value() + FIELDS_VALIDATION_ERROR.getCode())
                .errorMessage(ErrorMessage.builder()
                        .key(FIELDS_VALIDATION_ERROR.name())
                        .text(ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField().concat(":").concat(error.getDefaultMessage()))
                                .peek(log::error)
                                .collect(Collectors.joining(";")))
                        .build())
                .timestamp(Instant.now().toEpochMilli())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBody);
    }

    @ResponseBody
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        var responseBody = ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value() + FIELDS_VALIDATION_ERROR.getCode())
                .errorMessage(ErrorMessage.builder()
                        .key(FIELDS_VALIDATION_ERROR.name())
                        .text(ex.getConstraintViolations().stream()
                                .map(ConstraintViolation::getMessage)
                                .peek(log::error)
                                .collect(Collectors.joining(";")))
                        .build())
                .timestamp(Instant.now().toEpochMilli())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBody);
    }
}
