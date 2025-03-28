package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.ExchangeRequestDto;
import com.skillnez.model.dto.ExchangeResponseDto;
import com.skillnez.service.ExchangeRateService;
import com.skillnez.service.ExchangeService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    @Inject
    private ExchangeRateService exchangeRateService;
    @Inject
    private ExchangeService exchangeService;

    private final JsonMapper jsonMapper = new JsonMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        BigDecimal amount = exchangeRateService.extractValue(req.getParameter("amount"));
        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto(baseCurrency, targetCurrency, amount);
        ExchangeResponseDto exchangeResult = exchangeService.exchange(exchangeRequestDto);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeResult));
    }
}
