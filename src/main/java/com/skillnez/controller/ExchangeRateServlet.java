package com.skillnez.controller;

import com.skillnez.exceptions.IncorrectRequestException;
import com.skillnez.mapper.DtoMapper;
import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.ExchangeRateResponseDto;
import com.skillnez.model.entity.ExchangeRate;
import com.skillnez.service.CurrencyService;
import com.skillnez.service.ExchangeService;
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
    private final DtoMapper dtoMapper = new DtoMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write(jsonMapper.dtoToJson(exchangeService.getExchangeRateByCurrencyPair(req.getPathInfo())));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExchangeRateResponseDto exchangeRateResponseDto = exchangeService.getExchangeRateByCurrencyPair(req.getPathInfo());
        var rate = req.getParameter("rate");
        ExchangeRate exchangeRate = exchangeService.update(rate, exchangeRateResponseDto);
        resp.getWriter().write(jsonMapper.dtoToJson(dtoMapper.convertToExchangeRateResponseDto(exchangeRate)));
    }

}
