package com.skillnez.controller.servlets;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        try (var writer = resp.getWriter()) {
            writer.write("<h1>Список валют<h1>");
            writer.write("<ul>");
            currencyService.findAll().forEach(
                    currencyDto -> writer.write("<li>" + currencyDto.toString() + "</li>"));
            writer.write("</ul>");
        }

    }
}
