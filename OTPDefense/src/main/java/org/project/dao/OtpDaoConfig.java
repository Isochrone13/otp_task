package org.project.dao;

import org.project.model.OTPConfig;
import java.sql.*;

public class OtpDaoConfig {
    // Метод для получения текущей конфигурации OTP (1 строка в таблице)
    public OTPConfig getConfiguration() {
        String sql = "SELECT * FROM otp_configuration LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if(rs.next()){
                // Создаём объект конфигурации и заполняем его данными из БД
                OTPConfig config = new OTPConfig();
                config.setId(rs.getInt("id"));
                config.setCodeLength(rs.getInt("code_length"));
                config.setValidDurationSeconds(rs.getInt("valid_duration_seconds"));
                return config; // Возвращаем конфигурацию
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Если конфигурация не найдена или произошла ошибка
    }

    // Метод для обновления конфигурации OTP
    public boolean updateConfiguration(OTPConfig config) {
        String sql = "UPDATE otp_configuration SET code_length = ?, valid_duration_seconds = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Устанавливаем новые значения для длины кода и времени жизни OTP
            stmt.setInt(1, config.getCodeLength());
            stmt.setInt(2, config.getValidDurationSeconds());
            stmt.setInt(3, config.getId());
            int rows = stmt.executeUpdate();
            return rows > 0; // Если обновлено хотя бы одно поле, возвращаем true
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // В случае ошибки возвращаем false
        }
    }
}

