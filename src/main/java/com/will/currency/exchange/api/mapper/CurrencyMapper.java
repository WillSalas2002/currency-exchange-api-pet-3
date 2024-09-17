package com.will.currency.exchange.api.mapper;

import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.response.CurrencyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CurrencyMapper {

    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    Currency toEntity(CurrencyResponse currencyResponse);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    CurrencyResponse toResponse(Currency currency);

    List<CurrencyResponse> toResponseList(List<Currency> currencies);
}
