package com.skillnez.service;

import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.model.dto.CurrencyDto;
import com.skillnez.dao.CurrencyDao;
import com.skillnez.model.entity.Currency;

import java.util.List;

public class CurrencyService {

    public static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {
    }

    public static CurrencyService getINSTANCE() {
        return INSTANCE;
    }


    public List<CurrencyDto> findAll() throws DaoException {
        return currencyDao.findAll().stream().map(currency -> new CurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign())).toList();
    }

    public CurrencyDto findByCode(String code) throws CurrencyNotFoundException, DaoException {
            Currency currency = currencyDao.getCurrencyByCode(code).orElseThrow(CurrencyNotFoundException::new);
            return new CurrencyDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }
}
