package com.will.currency.exchange.api.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.will.currency.exchange.api.exception.BadRequest;
import com.will.currency.exchange.api.exception.DuplicateEntityException;
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
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final String MESSAGE_INVALID_PARAMETER = "Invalid parameter: %s";
    private static final String MESSAGE_INTERNAL_SERVER_ERROR = "Internal Server Error. Try again later.";
    public static final String PARAM_FULL_NAME = "fullName";
    public static final String PARAM_CODE = "code";
    public static final String PARAM_SIGN = "sign";

    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencies);
        } catch (SQLException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fullName = req.getParameter(PARAM_FULL_NAME);
        String code = req.getParameter(PARAM_CODE);
        String sign = req.getParameter(PARAM_SIGN);
        try {
            if (!Validation.isValidFullName(fullName)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAMETER, fullName));
                return;
            }
            if (!Validation.isValidCode(code)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAMETER, code));
                return;
            }
            if (!Validation.isValidSign(sign)) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, String.format(MESSAGE_INVALID_PARAMETER, sign));
                return;
            }
            CurrencyDTO currency = new CurrencyDTO(code.toUpperCase(), fullName, sign);
            CurrencyDTO currencyDTO = currencyService.save(currency);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currencyDTO);
        } catch (BadRequest err) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, err.getMessage());
        } catch (DuplicateEntityException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, err.getMessage());
        } catch (SQLException err) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, MESSAGE_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String messageInternalServerError) throws IOException {
        resp.setStatus(statusCode);
        objectMapper.writeValue(resp.getWriter(), new ErrorDTO(messageInternalServerError));
    }
}
