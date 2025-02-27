package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.response.CurrencyDTO;
import com.will.currency.exchange.api.response.ExchangeDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public abstract class ExchangeStrategy {
    protected final ExchangeRateRepository exchangeRateRepository;

    public ExchangeStrategy(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public abstract Optional<ExchangeDTO> exchange(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, BigDecimal amount) throws SQLException;

    protected ExchangeDTO calculateExchangeAmount(ExchangeRate exchangeRate, BigDecimal amount) {
        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
        return new ExchangeDTO(exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate(), amount, convertedAmount);
    }
}
