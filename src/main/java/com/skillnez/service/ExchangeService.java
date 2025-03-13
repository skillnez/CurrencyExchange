package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.Currency;
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
        return exchangeRateDao.findAll().stream().map(this::toExchangeRateDto).toList();
    }

    public ExchangeRateResponseDto save (ExchangeRateRequestDto exchangeRateRequestDto) throws DaoException {
            ExchangeRate exchangeRate = exchangeRateDao.save(new ExchangeRate(
                    getCurrencyIdByCode(exchangeRateRequestDto.baseCurrency()),
                    getCurrencyIdByCode(exchangeRateRequestDto.targetCurrency()),
                    exchangeRateRequestDto.rate()));
            return toExchangeRateDto(exchangeRate);
    }

    public ExchangeRateResponseDto toExchangeRateDto(ExchangeRate exchangeRate) throws DaoException {
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                dtoMapper.convertToCurrencyResponseDto(getCurrencyById(exchangeRate.getBaseCurrencyId())),
                dtoMapper.convertToCurrencyResponseDto(getCurrencyById(exchangeRate.getTargetCurrencyId())),
                exchangeRate.getRate());
    }


    public ExchangeRateResponseDto update (ExchangeRateRequestDto exchangeRateRequestDto) throws DaoException {
        ExchangeRate exchangeRate = exchangeRateDao.findByCurrencyIdPair(
                getCurrencyIdByCode(exchangeRateRequestDto.baseCurrency()),
                getCurrencyIdByCode(exchangeRateRequestDto.targetCurrency())).orElseThrow(
                        () -> new CurrencyNotFoundException("Currency pair not found"));
        exchangeRate.setRate(exchangeRateRequestDto.rate());
        return toExchangeRateDto(exchangeRateDao.update(exchangeRate));
    }

    public ExchangeRateResponseDto getExchangeRateByCodes (String baseCurrencyCode, String targetCurrencyCode) throws DaoException {
        int baseCurrencyId = getCurrencyIdByCode(baseCurrencyCode);
        int targetCurrencyId = getCurrencyIdByCode(targetCurrencyCode);
        ExchangeRate exchangeRate = exchangeRateDao.findByCurrencyIdPair(baseCurrencyId, targetCurrencyId).
                orElseThrow(() -> new CurrencyNotFoundException("Exchange rate not found"));
        return toExchangeRateDto(exchangeRate);
    }

    public BigDecimal extractValue (String value) {
        try {
            String rateFromParameter = value.replace("rate=", "");
            rateFromParameter = rateFromParameter.replace(",", ".");
            return new BigDecimal(rateFromParameter);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException("Incorrect rate request value");
        } catch (NullPointerException e) {
            throw new IncorrectRequestException("Rate not extracted from request");
        }
    }

    public int getCurrencyIdByCode(String currencyCode) throws DaoException {
        return currencyDao.getCurrencyByCode(currencyCode).
                orElseThrow(CurrencyNotFoundException::new).getId();
    }

    public Currency getCurrencyById (int id) throws DaoException {
        return currencyDao.findById(id).orElseThrow(CurrencyNotFoundException::new);
    }

    public String extractBaseCurrencyCode(String currencyPair) {
        if (currencyPair != null) {
            return currencyPair.substring(1, 4);
        } else {
            throw new IncorrectRequestException("Currency pair is empty");
        }
    }

    public String extractTargetCurrencyCode(String currencyPair) {
        if (currencyPair != null) {
            return currencyPair.substring(4, 7);
        } else {
            throw new IncorrectRequestException("Currency pair is empty");
        }
    }
}
