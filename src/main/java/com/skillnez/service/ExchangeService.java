package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.Currency;
import com.skillnez.model.entity.ExchangeRate;

import java.util.List;

public class ExchangeService {

    public static final ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final DtoMapper dtoMapper = new DtoMapper();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeService() {
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateResponseDto> getAllExchangeRates() throws DaoException {
        return exchangeRateDao.findAll().stream().map(exchangeRate -> new ExchangeRateResponseDto(
                exchangeRate.getId(),
                dtoMapper.convertToCurrencyResponseDto(exchangeRate.getBaseCurrencyId()),
                dtoMapper.convertToCurrencyResponseDto(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate())).toList();
    }

    public ExchangeRateResponseDto getExchangeRateByCurrencyPair(String currencyPair) throws DaoException {
        if (currencyPair.length() != 7) {
            throw new IncorrectRequestException("Incorrect currency pair request length");
        }
        String baseCurrencyCode = extractBaseCurrencyCode(currencyPair);
        String targetCurrencyCode = extractTargetCurrencyCode(currencyPair);
        int baseCurrencyId = getCurrencyIdByCode(baseCurrencyCode);
        int targetCurrencyId = getCurrencyIdByCode(targetCurrencyCode);
        return exchangeRateDao.findByCurrencyIdPair(baseCurrencyId, targetCurrencyId).map(exchangeRate ->
                new ExchangeRateResponseDto(
                        exchangeRate.getId(),
                        dtoMapper.convertToCurrencyResponseDto(exchangeRate.getBaseCurrencyId()),
                        dtoMapper.convertToCurrencyResponseDto(exchangeRate.getTargetCurrencyId()),
                        exchangeRate.getRate()
                )).orElseThrow(CurrencyNotFoundException::new);
    }

    private int getCurrencyIdByCode(String currencyCode) throws DaoException {
        return currencyDao.getCurrencyByCode(currencyCode).
                orElseThrow(CurrencyNotFoundException::new).getId();
    }

    private String extractBaseCurrencyCode(String currencyPair) {
        return currencyPair.substring(1, 4);
    }

    private String extractTargetCurrencyCode(String currencyPair) {
        return currencyPair.substring(4, 7);
    }
}
