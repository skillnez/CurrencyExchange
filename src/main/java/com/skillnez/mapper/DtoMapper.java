package com.skillnez.mapper;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.model.entity.Currency;

public class DtoMapper {

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public CurrencyResponseDto convertToCurrencyResponseDto (int currencyId) {
        Currency currency = currencyDao.findById(currencyId).orElseThrow(CurrencyNotFoundException::new);
        return new CurrencyResponseDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }

}
