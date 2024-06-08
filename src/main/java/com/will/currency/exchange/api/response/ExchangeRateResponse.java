package com.will.currency.exchange.api.response;

import com.will.currency.exchange.api.model.Currency;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        int id,
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate
) {
}
