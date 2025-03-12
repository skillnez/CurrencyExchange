package com.skillnez.controller;

import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.CurrencyRequestDto;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.ExchangeRate;
import com.skillnez.service.ExchangeService;
import com.skillnez.utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write(jsonMapper.listToJson(exchangeService.getAllExchangeRates()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrencyCode = req.getParameter("baseCurrencyCode");
        var targetCurrencyCode = req.getParameter("targetCurrencyCode");
        var rate = exchangeService.extractValue(req.getParameter("rate"));
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
        validator.validate(exchangeRateRequestDto);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeService.save(exchangeRateRequestDto)));
    }
}
