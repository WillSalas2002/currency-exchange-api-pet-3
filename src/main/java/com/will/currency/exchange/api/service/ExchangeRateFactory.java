package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.model.ExchangeRate;

import java.math.BigDecimal;

public class ExchangeRateFactory {
    public static ExchangeRate buildExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rate);
        return exchangeRate;
    }
}
