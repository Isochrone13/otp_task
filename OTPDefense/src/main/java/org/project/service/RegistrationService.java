package org.project.service;

import org.project.dao.UserDao;
import org.project.model.User;

public class RegistrationService {
    // Создаём экземпляр UserDao для работы с данными пользователей
    private UserDao userDao = new UserDao();

    // Простой метод хэширования пароля
    private String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    // Метод регистрации пользователя.
    public boolean registerUser(String login, String password, String role) {
        // Если регистрируется администратор, проверяем, существует ли уже администратор.
        if("ADMIN".equalsIgnoreCase(role)) {
            if(userDao.adminExists()) {
                // Если администратор уже зарегистрирован, выводим сообщение и возвращаем false.
                System.out.println("Администратор уже существует.");
                return false;
            }
        }
        // Создаем объект User и устанавливаем его свойства.
        User user = new User();
        user.setLogin(login);
        user.setHashedPassword(hashPassword(password));
        user.setRole(role.toUpperCase());
        // Вызываем метод создания пользователя в базе и возвращаем результат.
        return userDao.createUser(user);
    }
}

