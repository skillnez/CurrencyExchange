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

    public CurrencyResponseDto convertToCurrencyResponseDto (Currency currency) {
        return new CurrencyResponseDto(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
    }

}
