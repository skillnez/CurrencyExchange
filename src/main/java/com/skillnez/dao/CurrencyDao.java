package com.skillnez.dao;

import com.skillnez.exceptions.CurrencyAlreadyExistException;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.model.entity.Currency;
import com.skillnez.utils.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteErrorCode;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyDao implements Dao<Integer, Currency> {

    private static final Logger logger = LogManager.getLogger(CurrencyDao.class);
    private final DataSource dataSource;

    private static final String SAVE_SQL = """
            INSERT INTO Currencies (Code, FullName, Sign)
            VALUES (?, ?, ?)
            """;
    private static final String DELETE_SQL = """
            DELETE FROM Currencies
            WHERE ID = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT * FROM Currencies
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM Currencies
            WHERE ID = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE Currencies
            SET Code = ?, FullName = ?, Sign = ?
            WHERE ID = ?
            """;

    private static final String FIND_BY_CODE_SQL = """
            SELECT * FROM Currencies
            WHERE Code = ?
            """;

    private static final CurrencyDao INSTANCE = new CurrencyDao(ConnectionPool.getDataSource());

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private CurrencyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Currency save(Currency currency) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                currency.setId(keys.getInt(1));
            }
            return currency;
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            if (SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code == 2067) {
                logger.error("Duplicate ID violation: {}", e.getMessage());
                throw new CurrencyAlreadyExistException("Currency already exist");
            } else throw new DaoException("Error executing SQL query", e);
        }
    }

    public boolean delete(Integer id) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
    }

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
        return currencies;
    }

    public Optional<Currency> findById(Integer id) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
    }

    public Currency update(Currency currency) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.setInt(4, currency.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IncorrectRequestException("Can't update Currency");
            } else return currency;

        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
    }

    public Optional<Currency> getCurrencyByCode(String code) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            statement.setString(1, code);
            var resultSet = statement.executeQuery();
            Currency currency;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            } else {
                throw new CurrencyNotFoundException("Error executing SQL query");
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
    }

    private static Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("ID"),
                resultSet.getString("Code"),
                resultSet.getString("FullName"),
                resultSet.getString("Sign"));
    }
}

