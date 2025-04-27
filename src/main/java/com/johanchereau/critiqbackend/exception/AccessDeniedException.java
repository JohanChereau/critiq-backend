package com.johanchereau.critiqbackend.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends SecurityException {
    private final int errorCode;

    public AccessDeniedException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

