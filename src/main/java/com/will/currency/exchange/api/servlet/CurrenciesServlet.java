package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CurrencyResponse> currencies = currencyService.findAll();
        objectMapper.writeValue(resp.getWriter(), currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String fullName = req.getParameter("fullName");
        String sign = req.getParameter("sign");
        // TODO: need to add validation

        CurrencyResponse currency = new CurrencyResponse(0, code, fullName, sign);
        CurrencyResponse currencyResponse = currencyService.save(currency);
        objectMapper.writeValue(resp.getWriter(), currencyResponse);
    }
}
