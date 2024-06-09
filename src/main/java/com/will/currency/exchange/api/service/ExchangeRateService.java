package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.response.ExchangeRateResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateService {
    private final ExchangeRateRepository repository = new ExchangeRateRepository();

    public List<ExchangeRateResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::convertToExchangeRateResponse)
                .collect(Collectors.toList());
    }

    public ExchangeRateResponse convertToExchangeRateResponse(ExchangeRate exchangeRate) {
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate()
        );
    }
}
