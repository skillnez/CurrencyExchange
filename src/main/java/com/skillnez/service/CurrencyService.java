package com.skillnez.service;

import com.skillnez.dao.CurrencyDao;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.model.dto.CurrencyRequestDto;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.model.entity.Currency;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CurrencyService {

    @Inject
    private CurrencyDao currencyDao;

    public List<CurrencyResponseDto> findAll() throws DaoException {
        return currencyDao.findAll().stream().map(currency -> new CurrencyResponseDto(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign())).toList();
    }

    public CurrencyResponseDto findByCode(String code) throws CurrencyNotFoundException, DaoException {
        Currency currency = currencyDao.getCurrencyByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency with code " + code + " not found"));
        return new CurrencyResponseDto(currency.getId(),currency.getFullName(), currency.getCode(), currency.getSign());
    }

    public Currency save(CurrencyRequestDto currencyRequestDto) throws DaoException {
        String code = currencyRequestDto.code();
        String name = currencyRequestDto.name();
        String sign = currencyRequestDto.sign();
        Currency currency = new Currency(code, name, sign);
        return currencyDao.save(currency);
    }
}
