package com.skillnez.dao;

import com.skillnez.exceptions.CurrencyAlreadyExistException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.model.entity.ExchangeRate;
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

public class ExchangeRateDao implements Dao<Integer, ExchangeRate> {

    private static final Logger logger = LogManager.getLogger(ExchangeRateDao.class);
    private final DataSource dataSource;

    private final static String DELETE_SQL = """
            DELETE FROM ExchangeRates
            WHERE ID = ?
            """;
    private final static String FIND_ALL_SQL = """
            SELECT * FROM ExchangeRates
            """;
    private final static String FIND_BY_ID_SQL = """
            SELECT * FROM ExchangeRates
            WHERE ID = ?
            """;
    private final static String UPDATE_SQL = """
            UPDATE ExchangeRates
            SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ?
            WHERE ID = ?
            """;
    public final String SAVE_SQL = """
            INSERT INTO ExchangeRates (basecurrencyid, targetcurrencyid, rate)
            VALUES (?, ?, ?)
            """;
    private final static String FIND_BY_CURRENCY_ID_PAIR_SQL = """
            SELECT * FROM ExchangeRates
            WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
            """;
    private final static String FIND_BY_CURRENCY_CODES_PAIR_SQL = """
            SELECT e.ID,\s
            e.BaseCurrencyId,\s
            e.TargetCurrencyId,\s
            b.ID,\s
            b.Code,\s
            t.ID,\s
            t.Code,\s
            e.Rate
            FROM ExchangeRates e
            JOIN Currencies b ON e.BaseCurrencyId = b.ID
            JOIN Currencies t on e.TargetCurrencyId = t.ID
            WHERE\s
            (
                e.BaseCurrencyId = (SELECT c.id FROM Currencies c WHERE c.Code = ?) AND
                e.TargetCurrencyId = (SELECT c2.id FROM Currencies c2 WHERE c2.Code = ?)
            )
           \s""";

    private final static ExchangeRateDao INSTANCE = new ExchangeRateDao(ConnectionPool.getDataSource());

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    private ExchangeRateDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.setInt(4, exchangeRate.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new IncorrectRequestException("Nothing to update");
            } else
                return exchangeRate;
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(
                        buildExchangeRate(resultSet)
                );
            }
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
        return exchangeRates;
    }

    @Override
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

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, exchangeRate.getBaseCurrencyId());
            statement.setInt(2, exchangeRate.getTargetCurrencyId());
            statement.setBigDecimal(3, exchangeRate.getRate());
            statement.executeUpdate();
            var generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt(1));
            }
            return exchangeRate;
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            if (SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code == 2067) {
                logger.error("Duplicate ID violation: {}", e.getMessage());
                throw new CurrencyAlreadyExistException("Exchange rate already exist");
            } else throw new DaoException("Error executing SQL query", e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setInt(1, id);
            var resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new DaoException("Error executing SQL query", e);
        }
    }

    public Optional<ExchangeRate> findExchangePairByCurrencyId(int baseCurrencyId, int targetCurrencyId) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_BY_CURRENCY_ID_PAIR_SQL)) {
            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            var resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRate> findExchangePairByCurrencyCodes(String baseCode, String targetCode) {
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(FIND_BY_CURRENCY_CODES_PAIR_SQL)) {
            statement.setString(1, baseCode);
            statement.setString(2, targetCode);
            var resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("ID"),
                resultSet.getInt("BaseCurrencyId"),
                resultSet.getInt("TargetCurrencyId"),
                resultSet.getBigDecimal("Rate")
        );
    }
}
