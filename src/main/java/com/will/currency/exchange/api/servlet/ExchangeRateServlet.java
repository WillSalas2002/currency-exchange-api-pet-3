package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.response.ErrorDTO;
import com.will.currency.exchange.api.response.ExchangeRateDTO;
import com.will.currency.exchange.api.service.ExchangeRateService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final String PARAM_RATE = "rate";
    private static final String METHOD_PATCH = "PATCH";
    private static final String SYMBOL_FRONT_SLASH = "/";
    private static final String MESSAGE_INVALID_PARAMETER = "Invalid parameter: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later";

    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase(METHOD_PATCH)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, "");
        String baseCurrencyCode = pathInfo.substring(0, 3).toUpperCase();
        String targetCurrencyCode = pathInfo.substring(3).toUpperCase();

        try {
            if (!Validation.isValidCode(baseCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAMETER, baseCurrencyCode));
                return;
            }
            if (!Validation.isValidCode(targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAMETER, targetCurrencyCode));
                return;
            }
            ExchangeRateDTO exchangeRateDTO = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDTO);

        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (SQLException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, "");
        String baseCurrencyCode = pathInfo.substring(0, 3);
        String targetCurrencyCode = pathInfo.substring(3);
        Optional<String> rateOptional = getRateParameter(req);
        try {
            if (!Validation.isValidCode(baseCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, baseCurrencyCode);
                return;
            }
            if (!Validation.isValidCode(targetCurrencyCode)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, targetCurrencyCode);
                return;
            }
            if (rateOptional.isEmpty() || !Validation.isValidRate(rateOptional.get())) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, rateOptional.get());
                return;
            }
            BigDecimal rate = new BigDecimal(rateOptional.get());
            ExchangeRateDTO exchangeRate = exchangeRateService.findByCurrencyCodes(baseCurrencyCode.toUpperCase(), targetCurrencyCode.toUpperCase());
            exchangeRate.setRate(rate);
            ExchangeRateDTO updatedExchangeRate = exchangeRateService.update(exchangeRate);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), updatedExchangeRate);
        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (SQLException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String errorMessage) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorDTO(errorMessage));
    }

    private Optional<String> getRateParameter(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String rate = null;
        String[] split = sb.toString().split("&");
        for (String keyValue : split) {
            String[] s = keyValue.split("=");
            if (s[0].equals(PARAM_RATE)) {
                rate = s[1];
                break;
            }
        }
        return Optional.ofNullable(rate);
    }
}
