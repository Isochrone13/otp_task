package org.project.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailNotificationService implements NotificationService {
    private String username;
    private String password;
    private String fromEmail;
    private Session session;

    // Конструктор, где происходит загрузка конфигурации для работы с почтовым сервером из файла email.properties
    public EmailNotificationService() {
        try {
            // Создаём объект Properties и загружаем настройки из файла email.properties
            Properties config = new Properties();
            config.load(getClass().getClassLoader().getResourceAsStream("email.properties"));
            // Инициализируем переменные с параметрами аутентификации
            this.username = config.getProperty("email.username");
            this.password = config.getProperty("email.password");
            this.fromEmail = config.getProperty("email.from");

            // Создаём почтовую сессию с аутентификатором, который предоставляет учетные данные при необходимости
            session = Session.getInstance(config, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } catch (Exception e) {
            // В случае ошибки загрузки настроек выводим стек трейс
            e.printStackTrace();
        }
    }

    @Override
    public void sendCode(String destination, String code) {
        try {
            // Создаём сообщение электронной почты
            Message message = new MimeMessage(session);
            // Указываем отправителя
            message.setFrom(new InternetAddress(fromEmail));
            // Устанавливаем получателя
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination));
            // Задаём тему письма
            message.setSubject("Your OTP Code");
            // Формируем текст письма с OTP-кодом
            message.setText("Your verification code is: " + code);
            // Отправляем сообщение
            Transport.send(message);
            System.out.println("Email отправлен на " + destination);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
