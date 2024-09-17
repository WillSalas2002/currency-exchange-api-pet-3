package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.mapper.CurrencyMapper;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.repository.CurrencyRepository;
import com.will.currency.exchange.api.response.CurrencyResponse;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyRepository repository = new CurrencyRepository();
    private final CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;

    public List<CurrencyResponse> findAll() throws SQLException {
        return currencyMapper.toResponseList(repository.findAll());
    }

    public CurrencyResponse findByCurrencyCode(String currencyCode) throws SQLException {
        return repository.findByCurrencyCode(currencyCode)
                .map(currencyMapper::toResponse)
                .orElseThrow(() -> new NoSuchEntityException("There is no Currency with this code"));
    }

    public CurrencyResponse save(CurrencyResponse currencyResponse) throws SQLException {
        Currency currency = currencyMapper.toEntity(currencyResponse);
        Currency savedCurrency = repository.save(currency);
        return currencyMapper.toResponse(savedCurrency);
    }
}
