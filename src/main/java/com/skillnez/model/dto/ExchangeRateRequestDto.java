package com.skillnez.model.dto;

import java.math.BigDecimal;

public record ExchangeRateRequestDto(CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, BigDecimal rate){
}
