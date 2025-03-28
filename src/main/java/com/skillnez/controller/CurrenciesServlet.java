package com.skillnez.controller;

import com.skillnez.mapper.JsonMapper;
import com.skillnez.model.dto.CurrencyRequestDto;
import com.skillnez.model.dto.CurrencyResponseDto;
import com.skillnez.model.entity.Currency;
import com.skillnez.service.CurrencyService;
import com.skillnez.utils.Validator;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    @Inject
    private CurrencyService currencyService;

    private final JsonMapper jsonMapper = new JsonMapper();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> allCurrencies = currencyService.findAll();
        resp.getWriter().write(jsonMapper.listToJson(allCurrencies));
        resp.getWriter().flush();
        resp.getWriter().close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        CurrencyRequestDto currencyRequestDto = new CurrencyRequestDto(name, code, sign);
        validator.validate(currencyRequestDto);
        Currency currency = currencyService.save(currencyRequestDto);
        int id = currency.getId();
        CurrencyResponseDto currencyResponseDto = new CurrencyResponseDto(id, name, code, sign);
        resp.getWriter().write(jsonMapper.dtoToJson(currencyResponseDto));
    }
}
