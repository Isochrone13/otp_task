package org.project.dao;

import org.project.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    // Метод для создания нового пользователя в базе данных
    public boolean createUser(User user) {
        // Подготавливаем SQL‑запрос для вставки нового пользователя в таблицу users
        String sql = "INSERT INTO users (login, hashed_password, role) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Устанавливаем параметры: логин, зашифрованный пароль и роль пользователя
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getHashedPassword());
            stmt.setString(3, user.getRole());
            int rows = stmt.executeUpdate();
            return rows > 0; // Возвращаем true, если вставка прошла успешно
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для получения пользователя по логину
    public User getUserByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Устанавливаем логин в запрос
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                // Если пользователь найден, создаём объект User и заполняем его данными
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setLogin(rs.getString("login"));
                user.setHashedPassword(rs.getString("hashed_password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Если пользователь не найден, возвращаем null
    }

    // Метод для получения списка пользователей (не администраторов)
    public List<User> getNonAdminUsers() {
        List<User> users = new ArrayList<>();
        // SQL-запрос для выборки пользователей, у которых роль не 'ADMIN'
        String sql = "SELECT * FROM users WHERE role <> 'ADMIN'";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            // Проходим по полученным результатам и добавляем каждого пользователя в список
            while(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setLogin(rs.getString("login"));
                user.setHashedPassword(rs.getString("hashed_password"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users; // Возвращаем список пользователей
    }

    // Метод для удаления пользователя по его ID
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Устанавливаем ID пользователя, который нужно удалить
            stmt.setInt(1, userId);
            int rows = stmt.executeUpdate();
            return rows > 0; // Если удалена хотя бы одна запись, возвращаем true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для проверки, существует ли уже администратор в системе
    public boolean adminExists() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1); // Извлекаем количество администраторов
                return count > 0; // Возвращаем true, если хотя бы один администратор существует
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Если ошибка или администратор не найден, возвращаем false
    }
}
