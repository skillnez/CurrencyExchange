package com.skillnez.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    public final static String URL_KEY = "db.url";

    static {
        loadDriver();
    }

    private ConnectionManager() {
    }

    private static void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
