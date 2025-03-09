package com.skillnez.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    //вынесли параметры подключения в отдельные константы
    public final static String URL_KEY = "db.url";

    static {
        loadDriver();
    }

    //пустой конструктор
    private ConnectionManager() {
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // cоздаем свой метод для открытия соединения
    public static Connection open() {
        try {
            // возвращаем подключение к URL при помощи стандартных методов JDBC
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
