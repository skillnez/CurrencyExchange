package com.skillnez.model.dto;

import com.skillnez.model.entity.ExchangeRate;

import java.math.BigDecimal;

public record ExchangeResponseDto
        (CurrencyResponseDto baseCurrency,
         CurrencyResponseDto targetCurrency,
         BigDecimal rate,
         BigDecimal amount,
         BigDecimal convertedAmount ) {
}
