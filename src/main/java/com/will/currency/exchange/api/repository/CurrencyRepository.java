package com.will.currency.exchange.api.repository;

import com.will.currency.exchange.api.exception.DuplicateEntityException;
import com.will.currency.exchange.api.model.Currency;
import com.will.currency.exchange.api.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository {

    private final String SAVE_SQL = """
            INSERT INTO Currency(full_name, code, sign)
            VALUES(?, ?, ?);
            """;
    private final String FIND_ONE_SQL = """
            SELECT id, full_name, code, sign
            FROM Currency
            WHERE code = ?;
            """;
    private final String FIND_ALL_SQL = """
            SELECT id, full_name, code, sign
            FROM Currency;
            """;

    public Currency save(Currency currency) throws SQLException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getFullName());
            preparedStatement.setString(2, currency.getCode());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    currency.setId(generatedKeys.getInt(1));
                }
            }
            return currency;
        } catch (SQLException err) {
            if (err.getErrorCode() == 19) {
                throw new DuplicateEntityException("Currency with this code already exists", err);
            }
            throw new SQLException(err);
        }
    }

    public Optional<Currency> findByCurrencyCode(String code) throws SQLException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ONE_SQL)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        }
    }

    public List<Currency> findAll() throws SQLException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                Currency currency = buildCurrency(resultSet);
                currencies.add(currency);
            }
            return currencies;
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("code"),
                resultSet.getString("sign")
        );
    }
}
