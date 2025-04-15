package org.project.service;

import org.project.dao.UserDao;
import org.project.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    // Создаём объект UserDao для работы с данными пользователей
    private UserDao userDao = new UserDao();

    // Место для хранения токенов (ключ — токен; значение — логин пользователя)
    private static Map<String, String> tokenStore = new HashMap<>();

    // Метод для хэширования пароля
    private String hashPassword(String password) {
        return Integer.toString(password.hashCode());
    }

    // Метод для логина пользователя
    public String login(String login, String password) {
        // Получаем пользователя по логину из базы
        User user = userDao.getUserByLogin(login);
        // Если пользователь найден и зашифрованный пароль совпадает с хэшированным введённым паролем
        if(user != null && user.getHashedPassword().equals(hashPassword(password))) {
            // Генерируем случайный токен (UUID), который будет идентифицировать сессию пользователя
            String token = UUID.randomUUID().toString();
            // Сохраняем соответствие токена и логина в мапе
            tokenStore.put(token, login);
            return token; // Возвращаем сгенерированный токен
        }
        // Если аутентификация не удалась, возвращаем null
        return null;
    }

    // Метод для получения пользователя по токену аутентификации
    public User getUserByToken(String token) {
        // Извлекаем логин из нашего in-memory хранилища
        String login = tokenStore.get(token);
        if(login != null) {
            // Если логин найден, возвращаем пользователя из БД
            return userDao.getUserByLogin(login);
        }
        // Если токен недействителен, возвращаем null
        return null;
    }
}

