package com.skillnez.controller;

import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.ExchangeRate;
import com.skillnez.service.CurrencyService;
import com.skillnez.service.ExchangeService;
import com.skillnez.utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();
    private final DtoMapper dtoMapper = new DtoMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrencyCode = exchangeService.extractBaseCurrencyCode(req.getPathInfo());
        var targetCurrencyCode = exchangeService.extractTargetCurrencyCode(req.getPathInfo());
        ExchangeRateResponseDto exchangeRateResponseDto =
                exchangeService.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeRateResponseDto));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrencyCode = exchangeService.extractBaseCurrencyCode(req.getPathInfo());
        var targetCurrencyCode = exchangeService.extractTargetCurrencyCode(req.getPathInfo());
        var rate = exchangeService.extractValue(req.getParameter("rate"));
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
        validator.validate(exchangeRateRequestDto);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeService.update(exchangeRateRequestDto)));
    }

}
