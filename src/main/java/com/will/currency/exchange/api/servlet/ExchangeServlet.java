package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.InvalidParameterException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.response.ErrorResponse;
import com.will.currency.exchange.api.response.ExchangeResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.service.ExchangeService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = new ExchangeService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("base_currency_code");
        String targetCurrencyCode = req.getParameter("target_currency_code");
        String amountStr = req.getParameter("amount");

        try {
            if (!Validation.isValidCode(baseCurrencyCode) || !Validation.isValidCode(targetCurrencyCode)) {
                throw new InvalidParameterException("Invalid currency code entered");
            }

            if (!Validation.isValidRate(amountStr)) {
                throw new InvalidParameterException("Invalid currency amount entered");
            }
            CurrencyResponse baseCurrency = currencyService.findByCurrencyCode(baseCurrencyCode);
            CurrencyResponse targetCurrency = currencyService.findByCurrencyCode(targetCurrencyCode);
            BigDecimal amount = new BigDecimal(amountStr);
            Optional<ExchangeResponse> exchangeOptional = exchangeService.exchange(baseCurrency, targetCurrency, amount);
            if (exchangeOptional.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), exchangeOptional);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Couldn't convert"));
            }
        } catch (InvalidParameterException err) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (NoSuchEntityException err) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Database is not available"));
        }
    }
}
