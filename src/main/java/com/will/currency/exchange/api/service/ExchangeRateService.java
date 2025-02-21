package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.mapper.ExchangeRateMapper;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.response.ExchangeRateResponse;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private static final String MESSAGE_NOT_FOUND = "Exchange rate with this codes not found";
    private final ExchangeRateRepository repository = new ExchangeRateRepository();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;

    public List<ExchangeRateResponse> findAll() throws SQLException {
        return exchangeRateMapper.toResponseList(
                repository.findAll()
        );
    }

    public ExchangeRateResponse save(ExchangeRateResponse exchangeRateResponse) throws SQLException {
        ExchangeRate exchangeRate = exchangeRateMapper.toEntity(exchangeRateResponse);
        ExchangeRate savedExchangeRate = repository.save(exchangeRate);
        return exchangeRateMapper.toResponse(savedExchangeRate);
    }

    public ExchangeRateResponse findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return repository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .map(exchangeRateMapper::toResponse)
                .orElseThrow(() -> new NoSuchEntityException(MESSAGE_NOT_FOUND));
    }

    public ExchangeRateResponse update(ExchangeRateResponse exchangeRateResponse) throws SQLException {
        ExchangeRate exchangeRate = exchangeRateMapper.toEntity(exchangeRateResponse);
        ExchangeRate updatedExchangeRate = repository.update(exchangeRate);
        return exchangeRateMapper.toResponse(updatedExchangeRate);
    }
}
