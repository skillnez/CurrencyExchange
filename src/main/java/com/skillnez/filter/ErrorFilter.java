package com.skillnez.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skillnez.exceptions.CurrencyNotFoundException;
import com.skillnez.exceptions.DaoException;
import com.skillnez.mapper.JsonMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ErrorFilter extends HttpFilter {

    String errorMessage;

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException , ServletException {
        try {
            chain.doFilter(req, resp);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (DaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            errorMessage = "Database unavailable";
            writeError(resp, errorMessage);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errorMessage = "Incorrect JSON format";
            writeError(resp, errorMessage);
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorMessage = "Currency not found";
            writeError(resp, errorMessage);
        }
    }

    private void writeError(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"message\":\"" + errorMessage + "\"}");
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}
