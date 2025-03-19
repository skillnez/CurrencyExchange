package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.service.ExchangeRateService;
import com.skillnez.utils.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> allExchangeRates = exchangeRateService.getAllExchangeRates();
        resp.getWriter().write(jsonMapper.listToJson(allExchangeRates));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        BigDecimal rate = exchangeRateService.extractValue(req.getParameter("rate"));
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
        validator.validate(exchangeRateRequestDto);
        ExchangeRateResponseDto savedExchangeRate = exchangeRateService.save(exchangeRateRequestDto);
        resp.getWriter().write(jsonMapper.dtoToJson(savedExchangeRate));
    }
}
