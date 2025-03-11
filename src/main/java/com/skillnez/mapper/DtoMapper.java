package com.skillnez.mapper;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.model.dto.CurrencyRequestDto;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.Currency;
import com.skillnez.model.entity.ExchangeRate;

public class DtoMapper {

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public CurrencyResponseDto convertToCurrencyResponseDto (int currencyId) {
        Currency currency = currencyDao.findById(currencyId).orElseThrow(CurrencyNotFoundException::new);
        return new CurrencyResponseDto(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
    }

    public ExchangeRateResponseDto convertToExchangeRateResponseDto (ExchangeRate exchangeRate) {
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                convertToCurrencyResponseDto(exchangeRate.getBaseCurrencyId()),
                convertToCurrencyResponseDto(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate()
        );
    }

//    public ExchangeRateRequestDto convertToExchangeRateRequestDto (
//            String  baseCurrencyCode, String targetCurrencyCode, String rate) {
//        var currencyPair = baseCurrencyCode + targetCurrencyCode;
//       int baseCurrencyId =
//    }
}
