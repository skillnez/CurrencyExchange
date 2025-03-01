package com.skillnez.repository.dao;

import com.skillnez.model.entity.Currency;
import com.skillnez.exceptions.DaoException;
import com.skillnez.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Integer, Currency> {

    private final static String SAVE_SQL = """
            INSERT INTO Currencies (Code, FullName, Sign)
            VALUES (?, ?, ?)
            """;
    private final static String DELETE_SQL = """
            DELETE FROM Currencies
            WHERE ID = ?
            """;
    private final static String FIND_ALL_SQL = """
            SELECT * FROM Currencies
            """;
    private final static String FIND_BY_ID_SQL = """
            SELECT * FROM Currencies
            WHERE ID = ?
            """;

    private final static String UPDATE_SQL = """
            UPDATE Currencies
            SET Code = ?, FullName = ?, Sign = ?
            WHERE ID = ?
            """;
    //сделал класс синглтоном
    private final static CurrencyDao INSTANCE = new CurrencyDao();

    public CurrencyDao() {
    }

    private static Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("ID"),
                resultSet.getString("Code"),
                resultSet.getString("FullName"),
                resultSet.getString("Sign")
        );
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    public Currency save(Currency currency) {
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            //назначаем в SQL запрос вместо "?" значения нашей валюты, которые надо сохранить
            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                currency.setId(keys.getInt(1));
            }
            //Заполняем у валюты ID, который ранее был автоматически сгенерирован
            return currency;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean delete(Integer id) {
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Currency> findALl() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                currencies.add(
                        buildCurrency(resultSet)
                );
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return currencies;
    }

    public Optional<Currency> findById(Integer id) {
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean update(Currency currency) {
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.setInt(4, currency.getId());
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}

