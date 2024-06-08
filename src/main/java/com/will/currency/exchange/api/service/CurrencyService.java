package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.repository.CurrencyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private final CurrencyRepository repository = new CurrencyRepository();

    public List<CurrencyResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::buildCurrencyResponse)
                .collect(Collectors.toList());
    }

    public CurrencyResponse findByCurrencyCode(String currencyCode) {
        Optional<Currency> currencyOptional = repository.findByCurrencyCode(currencyCode);
        if (currencyOptional.isEmpty()) {
            throw new RuntimeException("There is no Currency with this code.");
        }
        return buildCurrencyResponse(currencyOptional.get());
    }

    private CurrencyResponse buildCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }
}
