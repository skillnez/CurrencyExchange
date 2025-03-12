package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.ExchangeRate;

import java.math.BigDecimal;
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

    public ExchangeRate save (ExchangeRateRequestDto exchangeRateRequestDto) throws DaoException {
            return exchangeRateDao.save(new ExchangeRate(
                    getCurrencyIdByCode(exchangeRateRequestDto.baseCurrency()),
                    getCurrencyIdByCode(exchangeRateRequestDto.targetCurrency()),
                    exchangeRateRequestDto.rate()));
    }


    public ExchangeRate update (String rate, ExchangeRateResponseDto exchangeRateResponseDto) throws DaoException {
        try {
            BigDecimal rateValue = new BigDecimal(rate);
            return exchangeRateDao.update(new ExchangeRate(
                    exchangeRateResponseDto.id(),
                    exchangeRateResponseDto.baseCurrency().id(),
                    exchangeRateResponseDto.targetCurrency().id(),
                    rateValue
            ));
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException("Incorrect rate request value");
        }
    }

    public int getCurrencyIdByCode(String currencyCode) throws DaoException {
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
