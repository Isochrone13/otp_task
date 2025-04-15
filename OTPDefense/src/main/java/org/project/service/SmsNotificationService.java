package org.project.service;

import java.util.Properties;  // Импорт класса для работы с настройками из файла

// Класс реализует NotificationService для отправки СМС.
// Для демонстрации используется эмуляция отправки, параметры подключения считываются из файла sms.properties.
public class SmsNotificationService implements NotificationService {
    // Объявляем переменные для хранения параметров подключения к SMPP-эмулятору
    private String host;
    private int port;
    private String systemId;
    private String password;
    private String systemType;
    private String sourceAddress;

    // Конструктор класса, в котором загружаются настройки из файла sms.properties.
    public SmsNotificationService() {
        try {
            // Создаем объект Properties для загрузки настроек.
            Properties config = new Properties();
            // Загружаем свойства из ресурса sms.properties (файл должен находиться в src/main/resources).
            config.load(getClass().getClassLoader().getResourceAsStream("sms.properties"));
            // Инициализируем переменные параметрами из файла.
            this.host = config.getProperty("smpp.host");
            // Преобразуем порт из строки в целое число.
            this.port = Integer.parseInt(config.getProperty("smpp.port"));
            this.systemId = config.getProperty("smpp.system_id");
            this.password = config.getProperty("smpp.password");
            this.systemType = config.getProperty("smpp.system_type");
            this.sourceAddress = config.getProperty("smpp.source_addr");
        } catch (Exception e) {
            // В случае ошибки загрузки настроек выводим информацию об исключении.
            e.printStackTrace();
        }
    }

    @Override
    public void sendCode(String destination, String code) {
        // Выводим в консоль сообщение о том, что СМС отправлено.
        System.out.println("СМС (эмуляция) отправлено на " + destination + ": Ваш код " + code);
    }
}

