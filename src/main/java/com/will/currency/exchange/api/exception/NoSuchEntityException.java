package com.will.currency.exchange.api.exception;

import lombok.Getter;

@Getter
public class NoSuchEntityException extends RuntimeException {
    private final String message;

    public NoSuchEntityException(String message) {
        super(message);
        this.message = message;
    }
}
