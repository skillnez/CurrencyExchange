package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.ExchangeRequestDto;
import com.skillnez.model.dto.ExchangeResponseDto;
import com.skillnez.model.entity.Currency;
import com.skillnez.model.entity.ExchangeRate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ExchangeService {

    public static final ExchangeService instance = new ExchangeService();
    private static final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final DtoMapper dtoMapper = new DtoMapper();

    public static ExchangeService getInstance() {
        return instance;
    }

    private ExchangeService() {
    }

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeDto) {
        BigDecimal rate;
        BigDecimal convertedAmount;
        ExchangeRate exchangeRate =
                exchangeRateDao.findExchangePairByCurrencyCodes(exchangeDto.baseCurrencyCode(), exchangeDto.targetCurrencyCode())
                        .orElseThrow(() -> new CurrencyNotFoundException("Exchange rate not found"));
//        //Сценарий прямого курса
        rate = exchangeRate.getRate();
        convertedAmount = rate.multiply(exchangeDto.amount()).setScale(2, RoundingMode.HALF_EVEN);
        //сценарий обратного курса
//        rate = getReverseRate(exchangeRate);
//        convertedAmount = rate.multiply(exchangeDto.amount()).setScale(2, RoundingMode.HALF_EVEN);
        //сценарий кросс курса
        return new ExchangeResponseDto(
                currencyDao.findById(exchangeRate.getBaseCurrencyId())
                        .map(dtoMapper::convertToCurrencyResponseDto)
                        .orElseThrow(CurrencyNotFoundException::new),
                currencyDao.findById(exchangeRate.getTargetCurrencyId())
                        .map(dtoMapper::convertToCurrencyResponseDto)
                        .orElseThrow(CurrencyNotFoundException::new),
                rate,
                exchangeDto.amount(),
                convertedAmount
        );
    }


//    public ExchangeResponseDto exchangeByCrossRate (ExchangeRequestDto exchangeDto) {
//        Currency primaryCurrency = currencyDao.getCurrencyByCode("USD").orElseThrow(() ->
//                new CurrencyNotFoundException("Currency: " + exchangeDto.baseCurrencyCode() + " not found"));
//        Currency baseCurrency = currencyDao.getCurrencyByCode(exchangeDto.baseCurrencyCode()).orElseThrow(() ->
//                new CurrencyNotFoundException("Currency: " + exchangeDto.baseCurrencyCode() + " not found"));
//        Currency targetCurrency = currencyDao.getCurrencyByCode(exchangeDto.targetCurrencyCode()).orElseThrow(() ->
//                new CurrencyNotFoundException("Currency: " + exchangeDto.targetCurrencyCode() + " not found"));
//
//    }


    private BigDecimal getReverseRate(ExchangeRate exchangeRate) {
        return BigDecimal.ONE.divide(exchangeRate.getRate(), MathContext.DECIMAL64).
                setScale(6, RoundingMode.HALF_EVEN);
    }
}
