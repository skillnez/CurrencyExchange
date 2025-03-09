package com.skillnez.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter("/*")
//@WebFilter("/*") - так можно указать что будет для всех урлов
//@WebFilter("servletNames = ..., ..., ") - можно указать нужные названия сервлетов, чтобы только к ним применялся фильтр
//Нельзя использовать и то и другое.
public class CharsetFilter implements Filter {
    //Фильтр автоматически перехватывает сервлеты с нужным названием или url, указанных в аннотации @webFilter.
    //В качестве имени сервлета в фильтре будет либо само название класса сервлета, либо его имя в аннотации @WebServlet
    //теперь этот фильтр, без какого либо указания в самом сервлете, будет применяться и выполнять нужную кодировку
    //при каждом вызове всех сервлетов в проекте. Нужно это например если мы не хотим чтобы сервлеты, где есть картинки
    //обрабатывались фильтрами для текста.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //обозначаем что будем делать в фильтре на запрос и ответ
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        //здесь можно передать по цепочке в следующий фильтр. Если этой стройки не будет написано,
        //обработка прервется на этом фильтре. Порядок обработки задается в web.xml.
        // Aннотацией @Priority в CDI или @Order - в spring
        //Удобно можно использовать с условными операторами, если что-то случилось - то return
        //Если не случилось, то вызываем chain.doFilter(request, response); и выполняем цепочку фильтров дальше
        chain.doFilter(request, response);
    }
}
