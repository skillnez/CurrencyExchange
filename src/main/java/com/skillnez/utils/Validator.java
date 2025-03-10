package com.skillnez.utils;

import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.model.dto.CurrencyRequestDto;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern VALID_INPUT_PATTERN =
            Pattern.compile("^[\\x20-\\x7E\\u00A2-\\u00A5\\u09F2\\u09F3\\u0E3F\\u17DB\\u20A0-" +
                            "\\u20CF\\u20B9\\uFE69\\uFF04\\uFFE0-\\uFFE6]*$");


    public void validate(CurrencyRequestDto requestDto) {
        validateField("code", requestDto.code(), 3);
        validateField("name", requestDto.name(), 25);
        validateField("sign", requestDto.sign(), 1);
    }

    private void validateField(String fieldName, String fieldValue, int maxLength) {
        if (fieldValue == null || fieldValue.isBlank()) {
            throw new IncorrectRequestException("Field '" + fieldName + "' is empty");
        }
        if (fieldValue.length() > maxLength) {
            throw new IncorrectRequestException("Field '" + fieldName + "' is too long");
        }
        if (!VALID_INPUT_PATTERN.matcher(fieldValue).matches()) {
            throw new IncorrectRequestException("Field '" + fieldName + "' contains invalid characters");
        }
    }

}
