package com.johanchereau.critiqbackend.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {
    private final int errorCode;

    public AccessDeniedException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

