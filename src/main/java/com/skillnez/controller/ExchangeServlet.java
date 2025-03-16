package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.ExchangeRequestDto;
import com.skillnez.service.ExchangeRateService;
import com.skillnez.service.ExchangeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    ExchangeService exchangeService = ExchangeService.getInstance();
    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    JsonMapper jsonMapper = new JsonMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrency = req.getParameter("from");
        var targetCurrency = req.getParameter("to");
        var amount = exchangeRateService.extractValue(req.getParameter("amount"));
        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto(baseCurrency, targetCurrency, amount);
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeService.exchangeByReverseRate(exchangeRequestDto)));
    }
}
