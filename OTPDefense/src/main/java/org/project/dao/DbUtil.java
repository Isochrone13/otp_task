package org.project.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

    // URL для подключения к базе данных PostgreSQL.
    // Здесь "localhost" – адрес сервера, "5432" – стандартный порт PostgreSQL,
    // "postgres" – имя базы данных (его можно заменить на другое, если используется другая база).
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";

    // Имя пользователя для подключения к базе данных.
    private static final String USER = "ENTER_YOUR_USERNAME";

    // Пароль для подключения к базе данных.
    private static final String PASSWORD = "ENTER_YOUR_PASSWORD";

    // Создаём подключение к БД с помощью getConnection()
    public static Connection getConnection() throws SQLException {
        // Используем класс DriverManager для получения подключения к базе.
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
