package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.DuplicateEntityException;
import com.will.currency.exchange.api.exception.InvalidParameterException;
import com.will.currency.exchange.api.response.CurrencyResponse;
import com.will.currency.exchange.api.response.ErrorResponse;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<CurrencyResponse> currencies = currencyService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencies);
        } catch (SQLException err) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse("Database is not available"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fullName = req.getParameter("fullName");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        try {
            if (!Validation.isValidFullName(fullName)) {
                throw new InvalidParameterException("Invalid [fullName] entered");
            }
            if (!Validation.isValidCode(code)) {
                throw new InvalidParameterException("Invalid [code] entered");
            }
            if (!Validation.isValidSign(sign)) {
                throw new InvalidParameterException("Invalid [sign] entered");
            }
            CurrencyResponse currency = new CurrencyResponse(code.toUpperCase(), fullName, sign);
            CurrencyResponse currencyResponse = currencyService.save(currency);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencyResponse);
        } catch (InvalidParameterException err) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
