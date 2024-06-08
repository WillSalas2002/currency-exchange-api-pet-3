package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.repository.CurrencyRepository;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {
    private final CurrencyRepository repository = new CurrencyRepository();

    public List<CurrencyResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(currency -> new CurrencyResponse(
                        currency.getId(),
                        currency.getFullName(),
                        currency.getCode(),
                        currency.getSign()))
                .collect(Collectors.toList());
    }
}
