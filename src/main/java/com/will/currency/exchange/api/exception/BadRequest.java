package com.will.currency.exchange.api.exception;

import lombok.Getter;

@Getter
public class BadRequest extends RuntimeException {
    private final String message;

    public BadRequest(String message) {
        super(message);
        this.message = message;
    }
}
