package com.skillnez.filter;

import com.skillnez.exceptions.DaoException;
import com.skillnez.mapper.JsonMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ErrorFilter implements Filter {

    String errorMessage;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            chain.doFilter(request, response);
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            errorMessage = "внутренняя ошибка сервера";
            writeError(httpResponse, errorMessage);
        }
    }

    private void writeError(HttpServletResponse httpResponse, String errorMessage) throws IOException {
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.getWriter().write("{\"message\":\"" + errorMessage + "\"}");
    }
}
