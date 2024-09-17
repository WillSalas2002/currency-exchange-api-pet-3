package com.will.currency.exchange.api.mapper;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRate toEntity(ExchangeRateResponse exchangeRateResponse);

    ExchangeRateResponse toResponse(ExchangeRate exchangeRate);

    List<ExchangeRateResponse> toResponseList(List<ExchangeRate> exchangeRates);
}
