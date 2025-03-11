package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.CurrencyRequestDto;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.model.entity.Currency;
import com.skillnez.service.CurrencyService;
import com.skillnez.utils.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getINSTANCE();
    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write(jsonMapper.listToJson(currencyService.findAll()));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var code = req.getParameter("code");
        var name = req.getParameter("name");
        var sign = req.getParameter("sign");

        CurrencyRequestDto currencyRequestDto = new CurrencyRequestDto(name, code, sign);
        validator.validate(currencyRequestDto);
        Currency currency = currencyService.save(currencyRequestDto);
        int id = currency.getId();
        CurrencyResponseDto currencyResponseDto = new CurrencyResponseDto(id, name, code, sign);
        resp.getWriter().write(jsonMapper.dtoToJson(currencyResponseDto));
    }
}
