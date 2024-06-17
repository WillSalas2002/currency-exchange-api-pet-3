package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.response.ExchangeRateResponse;
import com.will.currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
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

        // TODO: need to add validation and exception handling

        ExchangeRateResponse exchangeRateResponse = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        objectMapper.writeValue(resp.getWriter(), exchangeRateResponse);

    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> rateOptional = getRateParameter(req);
        String pathInfo = req.getPathInfo().replace("/", "");
        String baseCurrencyCode  = pathInfo.substring(0, 3);
        String targetCurrencyCode  = pathInfo.substring(3);
        if (rateOptional.isEmpty()) {
            throw new RuntimeException("Rate not given");
        }
        BigDecimal rate = new BigDecimal(rateOptional.get());
        ExchangeRateResponse exchangeRate = exchangeRateService.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
        exchangeRate.setRate(rate);
        ExchangeRateResponse updatedExchangeRate = exchangeRateService.update(exchangeRate);
        objectMapper.writeValue(resp.getWriter(), updatedExchangeRate);
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
