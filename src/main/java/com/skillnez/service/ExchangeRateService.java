package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.exceptions.ExchangeRateNotFoundException;
import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.Currency;
import com.skillnez.model.entity.ExchangeRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class ExchangeRateService {

    private ExchangeRateDao exchangeRateDao;
    private CurrencyDao currencyDao;

    public ExchangeRateService() {
    }

    @Inject
    public ExchangeRateService (ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    private final DtoMapper dtoMapper = new DtoMapper();

    public List<ExchangeRateResponseDto> getAllExchangeRates() throws DaoException {
        return exchangeRateDao.findAll().stream().map(this::convertToExchangeRateDto).toList();
    }

    public ExchangeRateResponseDto save (ExchangeRateRequestDto exchangeRateRequestDto) throws DaoException {
        int baseCurrencyId = getCurrencyIdByCode(exchangeRateRequestDto.baseCurrency());
        int targetCurrencyId = getCurrencyIdByCode(exchangeRateRequestDto.targetCurrency());
            ExchangeRate exchangeRate = exchangeRateDao.save(
                    new ExchangeRate(baseCurrencyId, targetCurrencyId, exchangeRateRequestDto.rate()));
            return convertToExchangeRateDto(exchangeRate);
    }

    public ExchangeRateResponseDto update (ExchangeRateRequestDto exchangeRateRequestDto) throws DaoException {
        ExchangeRate exchangeRate = getExchangeRate(exchangeRateRequestDto);
        exchangeRate.setRate(exchangeRateRequestDto.rate());
        return convertToExchangeRateDto(exchangeRateDao.update(exchangeRate));
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
            throw new IncorrectRequestException("Incorrect rate request value: " + value);
        } catch (NullPointerException e) {
            throw new IncorrectRequestException("Rate: " + value + " not extracted from request");
        }
    }

    public ExchangeRateResponseDto getExchangeRateDtoByCodes(String baseCurrencyCode, String targetCurrencyCode) throws DaoException {
        ExchangeRate exchangeRate = getExchangeRate(baseCurrencyCode, targetCurrencyCode);
        return convertToExchangeRateDto(exchangeRate);
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

    private ExchangeRateResponseDto convertToExchangeRateDto(ExchangeRate exchangeRate) throws DaoException {
        Currency baseCurrency = getCurrencyById(exchangeRate.getBaseCurrencyId());
        Currency targetCurrency = getCurrencyById(exchangeRate.getTargetCurrencyId());
        CurrencyResponseDto baseCurrencyDto = dtoMapper.convertToCurrencyResponseDto(baseCurrency);
        CurrencyResponseDto targetCurrencyDto = dtoMapper.convertToCurrencyResponseDto(targetCurrency);
        return new ExchangeRateResponseDto(exchangeRate.getId(), baseCurrencyDto, targetCurrencyDto, exchangeRate.getRate());
    }

    private ExchangeRate getExchangeRate(ExchangeRateRequestDto exchangeRateRequestDto) throws DaoException {
        int baseCurrencyId = getCurrencyIdByCode(exchangeRateRequestDto.baseCurrency());
        int targetCurrencyId = getCurrencyIdByCode(exchangeRateRequestDto.targetCurrency());
        return exchangeRateDao.findExchangePairByCurrencyId(baseCurrencyId, targetCurrencyId)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate %s %s not found"
                        .formatted(exchangeRateRequestDto.baseCurrency(), exchangeRateRequestDto.targetCurrency())));
    }

    private ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        int baseCurrencyId = getCurrencyIdByCode(baseCurrencyCode);
        int targetCurrencyId = getCurrencyIdByCode(targetCurrencyCode);
        ExchangeRate exchangeRate = exchangeRateDao.findExchangePairByCurrencyId(baseCurrencyId, targetCurrencyId).
                orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate %s %s not found"
                        .formatted(baseCurrencyCode, targetCurrencyCode)));
        return exchangeRate;
    }

    private int getCurrencyIdByCode(String currencyCode) throws DaoException {
        return currencyDao.getCurrencyByCode(currencyCode).
                orElseThrow(()
                        -> new CurrencyNotFoundException("Currency: " + currencyCode + " not found")).getId();
    }

    private Currency getCurrencyById (int id) throws DaoException {
        return currencyDao.findById(id).orElseThrow(()
                -> new CurrencyNotFoundException("Currency with id: " + id + " not found"));
    }
}
