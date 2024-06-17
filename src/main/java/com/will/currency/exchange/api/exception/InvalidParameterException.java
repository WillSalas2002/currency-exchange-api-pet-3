package com.will.currency.exchange.api.exception;

import lombok.Getter;

@Getter
public class InvalidParameterException extends RuntimeException {
    private final String message;
    public InvalidParameterException(String message) {
        super(message);
        this.message = message;
    }
}
