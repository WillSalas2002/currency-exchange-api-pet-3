package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.repository.CurrencyRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CurrencyService {
    private final CurrencyRepository repository = new CurrencyRepository();

    public List<CurrencyResponse> findAll() throws SQLException {
        return repository.findAll()
                .stream()
                .map(this::convertToCurrencyResponse)
                .collect(Collectors.toList());
    }

    public CurrencyResponse findByCurrencyCode(String currencyCode) {
        Optional<Currency> currencyOptional = repository.findByCurrencyCode(currencyCode);
        if (currencyOptional.isEmpty()) {
            throw new RuntimeException("There is no Currency with this code.");
        }
        return convertToCurrencyResponse(currencyOptional.get());
    }

    public CurrencyResponse save(CurrencyResponse currencyResponse) throws SQLException {
        Currency currency = convertToCurrency(currencyResponse);
        Currency savedCurrency = repository.save(currency);
        return convertToCurrencyResponse(savedCurrency);
    }

    private CurrencyResponse convertToCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }

    private Currency convertToCurrency(CurrencyResponse currencyResponse) {
        return new Currency(
                currencyResponse.getId(),
                currencyResponse.getCode(),
                currencyResponse.getFullName(),
                currencyResponse.getSign()
        );
    }
}
