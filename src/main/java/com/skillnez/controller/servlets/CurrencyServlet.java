package com.skillnez.controller.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skillnez.mapper.JsonMapper;
import com.skillnez.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/Currencies")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getINSTANCE();
    private final JsonMapper jsonMapper = new JsonMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (var writer = resp.getWriter()) {
            currencyService.findAll().forEach(
                    currencyDto -> {
                        try {
                            writer.write(jsonMapper.dtoToJson(currencyDto));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }
    }
}
