package com.will.currency.exchange.api.mapper;

import com.will.currency.exchange.api.model.ExchangeRate;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "baseCurrency", target = "baseCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "rate", target = "rate")
    ExchangeRate toEntity(ExchangeRateResponse exchangeRateResponse);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "baseCurrency", target = "baseCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    @Mapping(source = "rate", target = "rate")
    ExchangeRateResponse toResponse(ExchangeRate exchangeRate);

    List<ExchangeRateResponse> toResponseList(List<ExchangeRate> exchangeRates);
}
