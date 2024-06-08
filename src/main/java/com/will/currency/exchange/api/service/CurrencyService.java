package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.repository.CurrencyRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {
    private final CurrencyRepository repository = new CurrencyRepository();

    public List<CurrencyResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::buildCurrencyResponse)
                .collect(Collectors.toList());
    }

    private CurrencyResponse buildCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }
}
