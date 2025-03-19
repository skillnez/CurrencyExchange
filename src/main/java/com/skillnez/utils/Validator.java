package com.skillnez.utils;

import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.model.dto.CurrencyRequestDto;
import com.skillnez.model.dto.ExchangeRateRequestDto;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern VALID_INPUT_PATTERN =
            Pattern.compile("^[\\x20-\\x7E\\u00A2-\\u00A5\\u09F2\\u09F3\\u0E3F\\u17DB\\u20A0-" +
                            "\\u20CF\\u20B9\\uFE69\\uFF04\\uFFE0-\\uFFE6]*$");

    private static final int MAX_CURRENCY_CODE_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 25;
    private static final int MAX_SIGN_LENGTH = 2;

    public void validate(CurrencyRequestDto requestDto) {
        validateField("code", requestDto.code(), MAX_CURRENCY_CODE_LENGTH);
        validateField("name", requestDto.name(), MAX_NAME_LENGTH);
        validateField("sign", requestDto.sign(), MAX_SIGN_LENGTH);
    }

    public void validate (ExchangeRateRequestDto requestDto) {
        if (requestDto.baseCurrency().length() != MAX_CURRENCY_CODE_LENGTH ||
            requestDto.targetCurrency().length() != MAX_CURRENCY_CODE_LENGTH){
            throw new IncorrectRequestException("Incorrect currency code length");
        }
        if (!VALID_INPUT_PATTERN.matcher(requestDto.baseCurrency()).matches() ||
            !VALID_INPUT_PATTERN.matcher(requestDto.targetCurrency()).matches()){
            throw new IncorrectRequestException("One of currencies in request contains restricted symbols");
        }
        if (requestDto.rate() == null || requestDto.rate().compareTo(BigDecimal.ZERO) == 0) {
            throw new IncorrectRequestException("Rate cannot be null");
        }
        if (requestDto.rate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IncorrectRequestException("Rate cannot be negative");
        }
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
