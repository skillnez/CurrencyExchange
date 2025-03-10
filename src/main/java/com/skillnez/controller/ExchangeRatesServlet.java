package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.service.ExchangeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final JsonMapper jsonMapper = new JsonMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write(jsonMapper.listToJson(exchangeService.getAllExchangeRates()));
    }
}
