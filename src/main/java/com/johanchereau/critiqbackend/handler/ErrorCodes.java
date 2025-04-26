package com.johanchereau.critiqbackend.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login and/or password is incorrect"),
    ACCESS_DENIED(305, FORBIDDEN, "Access denied"),
    REVIEW_ALREADY_EXISTS(400, BAD_REQUEST, "You have already submitted a review for this movie"),
    ENTITY_NOT_FOUND(404, NOT_FOUND, "Resource not found"),
    ;

    @Getter
    private final int code;

    @Getter
    private final HttpStatus httpStatusCode;

    @Getter
    private final String message;

    ErrorCodes(int code, HttpStatus httpStatusCode, String message) {
        this.code = code;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }
}
