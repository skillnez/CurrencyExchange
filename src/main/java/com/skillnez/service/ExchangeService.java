package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.ExchangeRateNotFoundException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.ExchangeRequestDto;
import com.skillnez.model.dto.ExchangeResponseDto;
import com.skillnez.model.entity.ExchangeRate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {

    public static final ExchangeService instance = new ExchangeService();
    private static final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private static final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final DtoMapper dtoMapper = new DtoMapper();
    private final String CROSS_RATE_CONSTANT_CURRENCY = "USD";

    public static ExchangeService getInstance() {
        return instance;
    }

    private ExchangeService() {
    }

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeDto) {
        BigDecimal rate = BigDecimal.ZERO;
        BigDecimal convertedAmount =BigDecimal.ZERO;
        Optional<ExchangeRate> exchangeRate =
                exchangeRateDao.findExchangePairByCurrencyCodes(exchangeDto.baseCurrencyCode(), exchangeDto.targetCurrencyCode());
        Optional<ExchangeRate> reverseRate =
                exchangeRateDao.findExchangePairByCurrencyCodes(exchangeDto.targetCurrencyCode(), exchangeDto.baseCurrencyCode());
        Optional<ExchangeRate> usdToBase =
                exchangeRateDao.findExchangePairByCurrencyCodes(CROSS_RATE_CONSTANT_CURRENCY, exchangeDto.baseCurrencyCode());
        Optional<ExchangeRate> usdToTarget =
                exchangeRateDao.findExchangePairByCurrencyCodes(CROSS_RATE_CONSTANT_CURRENCY, exchangeDto.targetCurrencyCode());

        if (exchangeRate.isPresent()) {
            rate = exchangeRate.orElseThrow(ExchangeRateNotFoundException::new)
                    .getRate()
                    .setScale(6, RoundingMode.HALF_EVEN);
            convertedAmount = rate.multiply(exchangeDto.amount()).setScale(2, RoundingMode.HALF_EVEN);
        }
        if (exchangeRate.isEmpty() & reverseRate.isPresent()) {
            rate = getReverseRate(reverseRate.orElseThrow(ExchangeRateNotFoundException::new));
            convertedAmount = rate.multiply(exchangeDto.amount()).setScale(2, RoundingMode.HALF_EVEN);
        }
        if (usdToBase.isPresent() & usdToTarget.isPresent() & exchangeRate.isEmpty() & reverseRate.isEmpty()) {
            rate = getCrossRate(usdToBase.get(), usdToTarget.get());
            convertedAmount = rate.multiply(exchangeDto.amount()).setScale(2, RoundingMode.HALF_EVEN);
        }

        return new ExchangeResponseDto(
                currencyDao.getCurrencyByCode(exchangeDto.baseCurrencyCode())
                        .map(dtoMapper::convertToCurrencyResponseDto)
                        .orElseThrow(CurrencyNotFoundException::new),
                currencyDao.getCurrencyByCode(exchangeDto.targetCurrencyCode())
                        .map(dtoMapper::convertToCurrencyResponseDto)
                        .orElseThrow(CurrencyNotFoundException::new),
                rate,
                exchangeDto.amount(),
                convertedAmount
        );
    }

    private BigDecimal getCrossRate (ExchangeRate usdToTarget, ExchangeRate usdToBase) {
        return usdToBase.getRate()
                .divide(usdToTarget.getRate(), MathContext.DECIMAL64).setScale(6, RoundingMode.HALF_EVEN);
    }


    private BigDecimal getReverseRate(ExchangeRate exchangeRate) {
        return BigDecimal.ONE.divide(exchangeRate.getRate(), MathContext.DECIMAL64).
                setScale(6, RoundingMode.HALF_EVEN);
    }
}
