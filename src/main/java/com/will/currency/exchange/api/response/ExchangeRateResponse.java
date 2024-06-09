package com.will.currency.exchange.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ExchangeRateResponse {
    private int id;
    private CurrencyResponse baseCurrency;
    private CurrencyResponse targetCurrency;
    private BigDecimal rate;

    public ExchangeRateResponse(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
