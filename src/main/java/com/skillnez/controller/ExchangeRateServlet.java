package com.skillnez.controller;

import com.skillnez.mapper.DtoMapper;
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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();
    private final DtoMapper dtoMapper = new DtoMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("PATCH".equals(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrencyCode = exchangeRateService.extractBaseCurrencyCode(req.getPathInfo());
        var targetCurrencyCode = exchangeRateService.extractTargetCurrencyCode(req.getPathInfo());
        ExchangeRateResponseDto exchangeRateResponseDto =
                exchangeRateService.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeRateResponseDto));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        var baseCurrencyCode = exchangeRateService.extractBaseCurrencyCode(req.getPathInfo());
        var targetCurrencyCode = exchangeRateService.extractTargetCurrencyCode(req.getPathInfo());
        String parameter = req.getReader().readLine();
        var rate = exchangeRateService.extractValue(parameter);

        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
        validator.validate(exchangeRateRequestDto);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeRateService.update(exchangeRateRequestDto)));
    }

}
