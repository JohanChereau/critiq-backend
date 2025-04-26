package com.johanchereau.critiqbackend.handler;

import com.johanchereau.critiqbackend.exception.AccessDeniedException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.johanchereau.critiqbackend.handler.ErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity
                .status(ENTITY_NOT_FOUND.getHttpStatusCode())
                .body(ExceptionResponse.builder()
                        .errorCode(ENTITY_NOT_FOUND.getCode())
                        .errorMessage(ENTITY_NOT_FOUND.getMessage())
                        .errorDetails(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exception) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .errorCode(ACCOUNT_LOCKED.getCode())
                        .errorMessage(ACCOUNT_LOCKED.getMessage())
                        .errorDetails(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exception) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .errorCode(ACCOUNT_DISABLED.getCode())
                        .errorMessage(ACCOUNT_DISABLED.getMessage())
                        .errorDetails(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exception) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .errorCode(BAD_CREDENTIALS.getCode())
                        .errorMessage(BAD_CREDENTIALS.getMessage())
                        .errorDetails(BAD_CREDENTIALS.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exception) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .errorDetails(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception) {
        Set<String> errors = new HashSet<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            var errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(errors)
                        .build()
                );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalState(IllegalStateException ex) {
        if (REVIEW_ALREADY_EXISTS.getMessage().equals(ex.getMessage())) {
            return ResponseEntity
                    .status(REVIEW_ALREADY_EXISTS.getHttpStatusCode())
                    .body(ExceptionResponse.builder()
                            .errorCode(REVIEW_ALREADY_EXISTS.getCode())
                            .errorMessage(REVIEW_ALREADY_EXISTS.getMessage())
                            .errorDetails(ex.getMessage())
                            .build()
                    );
        }

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .errorCode(NO_CODE.getCode())
                        .errorMessage("Bad request")
                        .errorDetails(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity
                .status(FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .errorCode(exception.getErrorCode())
                        .errorMessage(ErrorCodes.ACCESS_DENIED.getMessage())
                        .errorDetails(exception.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {

        exception.printStackTrace();

        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .errorMessage("Internal server error. Please contact the admin")
                        .errorDetails(exception.getMessage())
                        .build()
                );
    }
}
