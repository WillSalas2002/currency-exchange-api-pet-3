package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.InvalidParameterException;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.response.ErrorResponse;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
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
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo().replace("/", "");
        String baseCurrencyCode = pathInfo.substring(0, 3).toUpperCase();
        String targetCurrencyCode = pathInfo.substring(3).toUpperCase();

        try {
            if (!Validation.isValidCode(baseCurrencyCode)) {
                throw new InvalidParameterException("Invalid [baseCurrencyCode] entered");
            }
            if (!Validation.isValidCode(targetCurrencyCode)) {
                throw new InvalidParameterException("Invalid [targetCurrencyCode] entered");
            }
            ExchangeRateResponse exchangeRateResponse = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRateResponse);

        } catch (InvalidParameterException err) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (NoSuchEntityException err) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (SQLException err) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Database is not available"));
        }

    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo().replace("/", "");
        String baseCurrencyCode = pathInfo.substring(0, 3);
        String targetCurrencyCode = pathInfo.substring(3);
        Optional<String> rateOptional = getRateParameter(req);

        try {
            if (!Validation.isValidCode(baseCurrencyCode)) {
                throw new InvalidParameterException("Invalid [baseCurrencyCode] entered");
            }
            if (!Validation.isValidCode(targetCurrencyCode)) {
                throw new InvalidParameterException("Invalid [targetCurrencyCode] entered");
            }
            if (rateOptional.isEmpty() || !Validation.isValidRate(rateOptional.get())) {
                throw new InvalidParameterException("Invalid [rate] entered");
            }
            BigDecimal rate = new BigDecimal(rateOptional.get());
            ExchangeRateResponse exchangeRate = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
            exchangeRate.setRate(rate);
            ExchangeRateResponse updatedExchangeRate = exchangeRateService.update(exchangeRate);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), updatedExchangeRate);

        } catch (InvalidParameterException err) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (NoSuchEntityException err) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse(err.getMessage()));
        } catch (SQLException err) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Database is not available"));
        }
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
            if (s[0].equals("rate")) {
                rate = s[1];
                break;
            }
        }
        return Optional.ofNullable(rate);
    }
}
