package com.skillnez.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skillnez.mapper.JsonMapper;
import com.skillnez.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getINSTANCE();
    private final JsonMapper jsonMapper = new JsonMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, JsonProcessingException {
           resp.getWriter().write(jsonMapper.listToJson(currencyService.findAll()));
           resp.getWriter().flush();
           resp.getWriter().close();
    }
}
