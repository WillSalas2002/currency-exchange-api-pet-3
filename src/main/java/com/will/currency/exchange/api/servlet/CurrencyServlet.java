package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.NoSuchEntityException;
import com.will.currency.exchange.api.response.CurrencyDTO;
import com.will.currency.exchange.api.response.ErrorDTO;
import com.will.currency.exchange.api.service.CurrencyService;
import com.will.currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final String SYMBOL_FRONT_SLASH = "/";
    private static final String MESSAGE_INVALID_PARAMETER = "Invalid parameter: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later";

    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyCode = req.getPathInfo().replace(SYMBOL_FRONT_SLASH, "");
        if (!Validation.isValidCode(currencyCode)) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, MESSAGE_INVALID_PARAMETER);
            return;
        }
        try {
            CurrencyDTO currencyDTO = currencyService.findByCurrencyCode(currencyCode.toUpperCase());
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencyDTO);
        } catch (NoSuchEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, err.getMessage());
        } catch (SQLException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String errorMessage) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorDTO(errorMessage));
    }
}
