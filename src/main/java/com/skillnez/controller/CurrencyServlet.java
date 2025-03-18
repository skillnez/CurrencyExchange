package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getINSTANCE();
    private final JsonMapper jsonMapper = new JsonMapper();

    public CurrencyServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currency = req.getPathInfo().substring(1);
        resp.getWriter().write(jsonMapper.dtoToJson(currencyService.findByCode(currency)));
    }
}
