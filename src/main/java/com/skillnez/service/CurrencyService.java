package com.skillnez.service;

import com.skillnez.exceptions.DaoException;
import com.skillnez.model.dto.CurrencyDto;
import com.skillnez.dao.CurrencyDao;

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
}
