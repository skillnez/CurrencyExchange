package com.skillnez.service;

import com.skillnez.dao.ExchangeRateDao;
import com.skillnez.exceptions.DaoException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.model.dto.ExchangeRateResponseDto;

import java.util.List;

public class ExchangeService {

    public static final ExchangeService INSTANCE = new ExchangeService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final DtoMapper dtoMapper = new DtoMapper();

    private ExchangeService() {
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRateResponseDto> getAllExchangeRates() throws DaoException {
        return exchangeRateDao.findAll().stream().map(exchangeRate -> new ExchangeRateResponseDto(
                exchangeRate.getId(),
                dtoMapper.convertToCurrencyResponseDto(exchangeRate.getBaseCurrencyId()),
                dtoMapper.convertToCurrencyResponseDto(exchangeRate.getTargetCurrencyId()),
                exchangeRate.getRate())).toList();
    }
}
