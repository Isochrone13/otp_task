package org.project.dao;

import org.project.model.OTP;
import org.project.model.OTPStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OtpDao {

    // Метод для создания новой записи OTP в базе данных
    public boolean createOTPCode(OTP otp) {
        // Подготавливаем SQL‑запрос на вставку новой записи в таблицу otp_codes
        String sql = "INSERT INTO otp_codes (operation_id, code, status, creation_time, expiration_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             // Используем PreparedStatement с опцией получения сгенерированных ключей
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Устанавливаем параметры запроса: операция, код, статус, время создания и время истечения OTP
            stmt.setString(1, otp.getOperationId());
            stmt.setString(2, otp.getCode());
            stmt.setString(3, otp.getStatus().name()); // Преобразуем enum в строку
            stmt.setTimestamp(4, Timestamp.valueOf(otp.getCreationTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(otp.getExpirationTime()));

            // Выполняем запрос. Если затронуто хотя бы одно поле - запрос успешен
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Извлекаем сгенерированный ключ (ID записи) и сохраняем его в объекте OTP
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    otp.setId(rs.getInt(1));
                }
                return true; // Возвращаем успешное выполнение метода
            }
        } catch (SQLException e) {
            // Печатаем стек ошибок, если произошла SQLException
            e.printStackTrace();
        }
        return false; // Если вставка не удалась, возвращаем false
    }

    // Метод для получения активного OTP по operationId и коду
    public OTP getOTPCode(String operationId, String code) {
        // Подготавливаем SQL-запрос для поиска записи с указанными operationId и code, только если статус ACTIVE
        String sql = "SELECT * FROM otp_codes WHERE operation_id = ? AND code = ? AND status = 'ACTIVE'";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Устанавливаем параметры запроса
            stmt.setString(1, operationId);
            stmt.setString(2, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Если запись найдена, создаём новый объект OTP и заполняем его данными из БД
                OTP otp = new OTP();
                otp.setId(rs.getInt("id"));
                otp.setOperationId(rs.getString("operation_id"));
                otp.setCode(rs.getString("code"));
                otp.setStatus(OTPStatus.valueOf(rs.getString("status")));
                otp.setCreationTime(rs.getTimestamp("creation_time").toLocalDateTime());
                otp.setExpirationTime(rs.getTimestamp("expiration_time").toLocalDateTime());
                return otp; // Возвращаем объект OTP
            }
        } catch (SQLException e) {
            // Обработка ошибок подключения или выполнения запроса
            e.printStackTrace();
        }
        return null; // Если запись не найдена или произошла ошибка, возвращаем null
    }

    // Метод для обновления статуса записи OTP по ее ID
    public boolean updateOTPStatus(int id, OTPStatus status) {
        // SQL-запрос для обновления статуса записи
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Устанавливаем новый статус и ID записи
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            return rows > 0; // Если хотя бы одна строка обновлена, возвращаем true
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // В случае ошибки возвращаем false
        }
    }

    // Метод для получения списка всех активных OTP
    public List<OTP> getActiveOTPCodes() {
        List<OTP> otps = new ArrayList<>();
        // SQL-запрос для выборки всех записей с активным статусом
        String sql = "SELECT * FROM otp_codes WHERE status = 'ACTIVE'";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            // Проходим по всем найденным записям и создаём объекты OTP, добавляя их в список
            while(rs.next()){
                OTP otp = new OTP();
                otp.setId(rs.getInt("id"));
                otp.setOperationId(rs.getString("operation_id"));
                otp.setCode(rs.getString("code"));
                otp.setStatus(OTPStatus.valueOf(rs.getString("status")));
                otp.setCreationTime(rs.getTimestamp("creation_time").toLocalDateTime());
                otp.setExpirationTime(rs.getTimestamp("expiration_time").toLocalDateTime());
                otps.add(otp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return otps; // Возвращаем список активных OTP
    }
}
