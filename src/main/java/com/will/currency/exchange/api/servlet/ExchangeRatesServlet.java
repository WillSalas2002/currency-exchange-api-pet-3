package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.DuplicateEntityException;
import com.will.currency.exchange.api.exception.InvalidParameterException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.response.ErrorResponse;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.ExchangeRateService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRateResponse> exchangeRates = exchangeRateService.findAll();
            objectMapper.writeValue(resp.getWriter(), exchangeRates);
        } catch (SQLException err) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Database is not available"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");
        try {
            if (!Validation.isValidCode(baseCurrencyCode)) {
                throw new InvalidParameterException("Invalid [baseCurrencyCode] entered");
            }
            if (!Validation.isValidCode(targetCurrencyCode)) {
                throw new InvalidParameterException("Invalid [targetCurrencyCode] entered");
            }
            if (!Validation.isValidRate(rateStr)) {
                throw new InvalidParameterException("Invalid [rate] entered");
            }
            baseCurrencyCode = baseCurrencyCode.toUpperCase();
            targetCurrencyCode = targetCurrencyCode.toUpperCase();
            BigDecimal rate = new BigDecimal(rateStr);

            CurrencyResponse baseCurrency = currencyService.findByCurrencyCode(baseCurrencyCode);
            CurrencyResponse targetCurrency = currencyService.findByCurrencyCode(targetCurrencyCode);
            ExchangeRateResponse exchangeRate = new ExchangeRateResponse(baseCurrency, targetCurrency, rate);

            ExchangeRateResponse savedExchangeRate = exchangeRateService.save(exchangeRate);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), savedExchangeRate);

        } catch (InvalidParameterException err) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (NoSuchEntityException err) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (DuplicateEntityException err) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (SQLException err) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Database is not available"));
        }
    }
}
