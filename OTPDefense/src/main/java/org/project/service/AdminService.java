package org.project.service;

import org.project.dao.OtpDaoConfig;
import org.project.dao.UserDao;
import org.project.model.OTPConfig;
import org.project.model.User;

import java.util.List;

public class AdminService {
    // Создаём объект для работы с конфигурацией OTP в базе
    private OtpDaoConfig otpConfigDao = new OtpDaoConfig();
    // Создаём объект для работы с данными пользователей
    private UserDao userDao = new UserDao();

    // Метод для изменения конфигурации OTP
    // codeLength - новая длина кода, validDurationSeconds новое время жизни OTP в секундах
    public boolean changeOtpConfiguration(int codeLength, int validDurationSeconds) {
        // Получаем текущую конфигурацию OTP из базы
        OTPConfig config = otpConfigDao.getConfiguration();
        if (config == null) {
            // Если конфигурация не найдена, возвращаем false
            return false;
        }
        // Обновляем свойства конфигурации
        config.setCodeLength(codeLength);
        config.setValidDurationSeconds(validDurationSeconds);
        // Сохраняем обновлённую конфигурацию и возвращаем результат операции
        return otpConfigDao.updateConfiguration(config);
    }

    // Вызываем метод для получения списка пользователей, не являющихся администраторами
    public List<User> listNonAdminUsers() {
        return userDao.getNonAdminUsers();
    }

    // Вызываем метод удаления пользователя по ID.
    public boolean deleteUserById(int userId) {
        return userDao.deleteUser(userId);
    }
}

