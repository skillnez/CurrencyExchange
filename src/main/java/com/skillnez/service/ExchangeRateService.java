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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExchangeRateService {

    public static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final DtoMapper dtoMapper = new DtoMapper();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public static ExchangeRateService getInstance() {
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
        ExchangeRate exchangeRate = exchangeRateDao.findExchangePairByCurrencyId(
                getCurrencyIdByCode(exchangeRateRequestDto.baseCurrency()),
                getCurrencyIdByCode(exchangeRateRequestDto.targetCurrency())).orElseThrow(
                        () -> new CurrencyNotFoundException("Currency pair not found"));
        exchangeRate.setRate(exchangeRateRequestDto.rate());
        return toExchangeRateDto(exchangeRateDao.update(exchangeRate));
    }

    public ExchangeRateResponseDto getExchangeRateByCodes (String baseCurrencyCode, String targetCurrencyCode) throws DaoException {
        int baseCurrencyId = getCurrencyIdByCode(baseCurrencyCode);
        int targetCurrencyId = getCurrencyIdByCode(targetCurrencyCode);
        ExchangeRate exchangeRate = exchangeRateDao.findExchangePairByCurrencyId(baseCurrencyId, targetCurrencyId).
                orElseThrow(() -> new CurrencyNotFoundException("Exchange rate not found"));
        return toExchangeRateDto(exchangeRate);
    }

    public BigDecimal extractValue (String value) {
        try {
            String regex = "[^0-9,.\\s]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            String rateFromParameter = matcher.replaceAll("");
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
                orElseThrow(()
                        -> new CurrencyNotFoundException("Currency: " + currencyCode + " not found")).getId();
    }

    public Currency getCurrencyById (int id) throws DaoException {
        return currencyDao.findById(id).orElseThrow(()
                -> new CurrencyNotFoundException("Currency with id: " + id + " not found"));
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
