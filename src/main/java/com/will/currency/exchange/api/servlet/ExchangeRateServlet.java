package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo().replace("/", "");
        String baseCurrencyCode = pathInfo.substring(0, 3).toUpperCase();
        String targetCurrencyCode = pathInfo.substring(3).toUpperCase();

        // TODO: need to add validation

        ExchangeRateResponse exchangeRateResponse = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        objectMapper.writeValue(resp.getWriter(), exchangeRateResponse);

    }
}
