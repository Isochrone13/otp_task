package org.project.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {
    public static void init() {
        try (Connection conn = DbUtil.getConnection()) {
            Statement stmt = conn.createStatement();

            // Создаем таблицу пользователей, если она не существует
            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "login VARCHAR(50) UNIQUE NOT NULL, " +
                    "hashed_password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(10) NOT NULL" +
                    ")";
            stmt.executeUpdate(createUsers);

            // Создаем таблицу конфигурации OTP, если она не существует
            String createOtpConfig = "CREATE TABLE IF NOT EXISTS otp_configuration (" +
                    "id SERIAL PRIMARY KEY, " +
                    "code_length INTEGER NOT NULL, " +
                    "valid_duration_seconds INTEGER NOT NULL" +
                    ")";
            stmt.executeUpdate(createOtpConfig);

            // Создаем таблицу OTP-кодов, если она не существует
            String createOtpCodes = "CREATE TABLE IF NOT EXISTS otp_codes (" +
                    "id SERIAL PRIMARY KEY, " +
                    "operation_id VARCHAR(100) NOT NULL, " +
                    "code VARCHAR(20) NOT NULL, " +
                    "status VARCHAR(10) NOT NULL, " +
                    "creation_time TIMESTAMP NOT NULL, " +
                    "expiration_time TIMESTAMP NOT NULL" +
                    ")";
            stmt.executeUpdate(createOtpCodes);

            // Проверяем, есть ли запись в таблице otp_configuration
            String checkConfig = "SELECT COUNT(*) FROM otp_configuration";
            ResultSet rs = stmt.executeQuery(checkConfig);
            int count = 0;
            if(rs.next()) {
                count = rs.getInt(1);
            }
            // Если в таблице нет записей, вставляем запись с настройками по умолчанию
            if(count == 0) {
                String insertConfig = "INSERT INTO otp_configuration (code_length, valid_duration_seconds) VALUES (6, 300)";
                stmt.executeUpdate(insertConfig);
                System.out.println("Таблица otp_configuration заполнена значениями по умолчанию.");
            }
            System.out.println("Схема базы данных успешно инициализирована.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

