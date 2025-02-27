package com.will.currency.exchange.api.service;

import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.mapper.ExchangeRateMapper;
import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.repository.ExchangeRateRepository;
import com.will.currency.exchange.api.response.ExchangeRateDTO;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private static final String MESSAGE_NOT_FOUND = "Exchange rate with this codes not found";
    private final ExchangeRateRepository repository = new ExchangeRateRepository();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;

    public List<ExchangeRateDTO> findAll() throws SQLException {
        return exchangeRateMapper.toResponseList(
                repository.findAll()
        );
    }

    public ExchangeRateDTO save(ExchangeRateDTO exchangeRateDTO) throws SQLException {
        ExchangeRate exchangeRate = exchangeRateMapper.toEntity(exchangeRateDTO);
        ExchangeRate savedExchangeRate = repository.save(exchangeRate);
        return exchangeRateMapper.toResponse(savedExchangeRate);
    }

    public ExchangeRateDTO findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return repository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode)
                .map(exchangeRateMapper::toResponse)
                .orElseThrow(() -> new NoSuchEntityException(MESSAGE_NOT_FOUND));
    }

    public ExchangeRateDTO update(ExchangeRateDTO exchangeRateDTO) throws SQLException {
        ExchangeRate exchangeRate = exchangeRateMapper.toEntity(exchangeRateDTO);
        ExchangeRate updatedExchangeRate = repository.update(exchangeRate);
        return exchangeRateMapper.toResponse(updatedExchangeRate);
    }
}
