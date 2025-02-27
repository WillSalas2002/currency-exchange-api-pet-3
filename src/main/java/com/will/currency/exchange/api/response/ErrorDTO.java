package com.will.currency.exchange.api.response;

import lombok.Getter;

@Getter
public class ErrorDTO {
    private final String message;

    public ErrorDTO(String message) {
        this.message = message;
    }
}
