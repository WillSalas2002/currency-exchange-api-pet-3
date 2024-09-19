package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.response.ExchangeResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();

    public Optional<ExchangeResponse> exchange(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency, BigDecimal amount) throws SQLException {

        Optional<ExchangeRate> directExchangeRateOptional = buildDirectExchangeRate(baseCurrency, targetCurrency);
        if (directExchangeRateOptional.isPresent()) {
            return Optional.of(calculateExchangeAmount(directExchangeRateOptional.get(), amount));
        }

        Optional<ExchangeRate> reversedExchangeRateOptional = buildReversedExchangeRate(baseCurrency, targetCurrency);
        if (reversedExchangeRateOptional.isPresent()) {
            return Optional.of(calculateReversedExchangeAmount(reversedExchangeRateOptional.get(), amount));
        }

        Optional<ExchangeRate> rateWithUSDBaseOptional = buildExchangeRateUsingUSD(baseCurrency, targetCurrency);
        if (rateWithUSDBaseOptional.isPresent()) {
            return Optional.of(calculateExchangeAmount(rateWithUSDBaseOptional.get(), amount));
        }

        return Optional.empty();
    }

    private Optional<ExchangeRate> buildDirectExchangeRate(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency) throws SQLException {
        return exchangeRateRepository.findByCurrencyCodes(baseCurrency.getCode(), targetCurrency.getCode());
    }

    private Optional<ExchangeRate> buildReversedExchangeRate(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency) throws SQLException {
        return exchangeRateRepository.findByCurrencyCodes(targetCurrency.getCode(), baseCurrency.getCode());
    }

    private Optional<ExchangeRate> buildExchangeRateUsingUSD(CurrencyResponse baseCurrency, CurrencyResponse targetCurrency) throws SQLException {
        final String USD_CODE = "USD";
        Optional<ExchangeRate> baseToUsdRate = exchangeRateRepository.findByCurrencyCodes(USD_CODE, baseCurrency.getCode());
        Optional<ExchangeRate> targetToUsdRate = exchangeRateRepository.findByCurrencyCodes(USD_CODE, targetCurrency.getCode());
        if (baseToUsdRate.isPresent() && targetToUsdRate.isPresent()) {
            BigDecimal rateWithUSDBase = targetToUsdRate.get().getRate().divide(baseToUsdRate.get().getRate(), 2, RoundingMode.HALF_EVEN);
            ExchangeRate exchangeRate = buildExchangeRate(baseToUsdRate.get().getBaseCurrency(), targetToUsdRate.get().getTargetCurrency(), rateWithUSDBase);
            return Optional.of(exchangeRate);
        }
        return Optional.empty();
    }

    private ExchangeResponse calculateReversedExchangeAmount(ExchangeRate reversedExchangeRate, BigDecimal amount) {
        BigDecimal reversedRate = BigDecimal.ONE.divide(reversedExchangeRate.getRate(), 4, RoundingMode.HALF_EVEN);
        ExchangeRate exchangeRate = buildExchangeRate(reversedExchangeRate.getTargetCurrency(), reversedExchangeRate.getBaseCurrency(), reversedRate);
        return calculateExchangeAmount(exchangeRate, amount);
    }

    private ExchangeResponse calculateExchangeAmount(ExchangeRate exchangeRate, BigDecimal amount) {
        BigDecimal convertedAmount = exchangeRate.getRate().multiply(amount).setScale(2, RoundingMode.HALF_EVEN);
        return new ExchangeResponse(exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate(), amount, convertedAmount);
    }

    private ExchangeRate buildExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rate);
        return exchangeRate;
    }
}
