package com.skillnez.model.entity;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRate {
    private int id;
    private int baseCurrencyId;
    private int targetCurrencyId;
    private BigDecimal rate;

    public ExchangeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }
}
