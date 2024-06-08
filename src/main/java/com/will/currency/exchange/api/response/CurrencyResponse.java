package com.will.currency.exchange.api.response;

public record CurrencyResponse(
        int id,
        String code,
        String fullName,
        String sign
) {
}
