package com.will.currency.exchange.api.response;

public record CurrencyResponse(
        int id,
        String fullName,
        String code,
        String sign
) {
}
