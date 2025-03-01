package com.skillnez.model.dto;

public record CurrencyDto(int id, String code, String name, String currency) {
    @Override
    public String toString() {
        return "CurrencyDto{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", name='" + name + '\'' +
               ", currency='" + currency + '\'' +
               '}';
    }
}
