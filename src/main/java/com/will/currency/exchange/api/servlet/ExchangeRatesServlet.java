package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRateResponse> exchangeRates = exchangeRateService.findAll();
        objectMapper.writeValue(resp.getWriter(), exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");

        // TODO: need to add validation of parameters

        baseCurrencyCode = baseCurrencyCode.toUpperCase();
        targetCurrencyCode = targetCurrencyCode.toUpperCase();
        BigDecimal rate = new BigDecimal(rateStr);
        CurrencyResponse baseCurrency = currencyService.findByCurrencyCode(baseCurrencyCode);
        CurrencyResponse targetCurrency = currencyService.findByCurrencyCode(targetCurrencyCode);
        ExchangeRateResponse exchangeRate = new ExchangeRateResponse(baseCurrency, targetCurrency, rate);
        ExchangeRateResponse savedExchangeRate = exchangeRateService.save(exchangeRate);

        objectMapper.writeValue(resp.getWriter(), savedExchangeRate);
    }
}
