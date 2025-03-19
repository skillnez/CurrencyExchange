package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.service.CurrencyService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getINSTANCE();
    private final JsonMapper jsonMapper = new JsonMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currencyRequest = extractCurrencyFromPath(req);
        CurrencyResponseDto requestedCurrency = currencyService.findByCode(currencyRequest);
        resp.getWriter().write(jsonMapper.dtoToJson(requestedCurrency));
    }

    private String extractCurrencyFromPath(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }
}
