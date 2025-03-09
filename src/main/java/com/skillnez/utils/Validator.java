package com.skillnez.utils;

import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.model.dto.CurrencyRequestDto;

public class Validator {

    public void validate(CurrencyRequestDto requestDto) {
        if (requestDto.code() == null || requestDto.code().isBlank()) {
            throw new IncorrectRequestException("Field 'code' is empty");
        }
        if (requestDto.name() == null || requestDto.name().isBlank()) {
            throw new IncorrectRequestException("Field 'name' is empty");
        }
        if (requestDto.sign() == null || requestDto.sign().isBlank()) {
            throw new IncorrectRequestException("Field 'sign' is empty");
        }
    }

}
