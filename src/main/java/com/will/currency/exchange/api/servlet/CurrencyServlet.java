package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends BaseServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCode = req.getPathInfo().replace("/", "").toUpperCase();
        // TODO: need to add validation
        CurrencyResponse currencyResponse = currencyService.findByCurrencyCode(currencyCode);
        objectMapper.writeValue(resp.getWriter(), currencyResponse);
    }
}
