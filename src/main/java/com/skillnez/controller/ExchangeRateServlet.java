package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.ExchangeRateRequestDto;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.service.ExchangeRateService;
import com.skillnez.utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("PATCH".equals(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = exchangeRateService.extractBaseCurrencyCode(req.getPathInfo());
        String targetCurrencyCode = exchangeRateService.extractTargetCurrencyCode(req.getPathInfo());
        ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.getExchangeRateDtoByCodes(baseCurrencyCode, targetCurrencyCode);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeRateResponseDto));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = exchangeRateService.extractBaseCurrencyCode(req.getPathInfo());
        String targetCurrencyCode = exchangeRateService.extractTargetCurrencyCode(req.getPathInfo());
        String rateRequestParameter = req.getReader().readLine();
        BigDecimal rate = exchangeRateService.extractValue(rateRequestParameter);
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
        validator.validate(exchangeRateRequestDto);
        ExchangeRateResponseDto updateResponse = exchangeRateService.update(exchangeRateRequestDto);
        resp.getWriter().write(jsonMapper.dtoToJson(updateResponse));
    }

}
