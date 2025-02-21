package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.BadRequest;
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
    private static final String PARAM_AMOUNT = "amount";
    private static final String PARAM_BASE_CURRENCY_CODE = "baseCurrencyCode";
    private static final String PARAM_TARGET_CURRENCY_CODE = "targetCurrencyCode";
    private static final String MESSAGE_INVALID_PARAMETER = "Invalid parameter: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later";

    private final ExchangeService exchangeService = new ExchangeService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter(PARAM_BASE_CURRENCY_CODE);
        String targetCurrencyCode = req.getParameter(PARAM_TARGET_CURRENCY_CODE);
        String amountStr = req.getParameter(PARAM_AMOUNT);

        try {
            if (!Validation.isValidCode(baseCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, String.format(MESSAGE_INVALID_PARAMETER, baseCurrencyCode));
            }
            if (!Validation.isValidCode(targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, String.format(MESSAGE_INVALID_PARAMETER, targetCurrencyCode));
            }
            if (!Validation.isValidRate(amountStr)) {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, String.format(MESSAGE_INVALID_PARAMETER, amountStr));
            }
            CurrencyResponse baseCurrency = currencyService.findByCurrencyCode(baseCurrencyCode);
            CurrencyResponse targetCurrency = currencyService.findByCurrencyCode(targetCurrencyCode);
            BigDecimal amount = new BigDecimal(amountStr);
            Optional<ExchangeResponse> exchangeOptional = exchangeService.exchange(baseCurrency, targetCurrency, amount);
            if (exchangeOptional.isPresent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), exchangeOptional.get());
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Couldn't convert");
            }
        } catch (BadRequest err) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, err.getMessage());
        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (SQLException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String messageInternalServerError) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorResponse(messageInternalServerError));
    }
}
