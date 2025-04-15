package org.project.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Класс для отправки OTP-кода через Telegram.
 * Настройки (botToken и базовый URL API) загружаются из файла telegram.properties,
 * находящегося в src/main/resources.
 * Если передан идентификатор в формате @username, метод getChatIdByUsername() используется для поиска числового chat_id через getUpdates.
 */
public class TelegramNotificationService implements NotificationService {
    private String botToken;
    private String telegramApiUrl; // URL для отправки сообщений через Telegram API (https://api.telegram.org/bot<BOT_TOKEN>/sendMessage)
    private String getUpdatesUrl;  // URL для получения обновлений (getUpdates)

    public TelegramNotificationService() {
        try {
            Properties config = new Properties();
            // Загружаем настройки из файла telegram.properties (файл должен находиться в classpath)
            config.load(getClass().getClassLoader().getResourceAsStream("telegram.properties"));
            this.botToken = config.getProperty("telegram.botToken");
            String baseUrl = config.getProperty("telegram.apiUrl", "https://api.telegram.org");
            // Формируем URL для отправки сообщений
            this.telegramApiUrl = baseUrl + "/bot" + botToken + "/sendMessage";
            // Формируем URL для получения обновлений
            this.getUpdatesUrl = baseUrl + "/bot" + botToken + "/getUpdates";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения числового chat_id по имени пользователя с использованием getUpdates.
     * Сравнивает введенное имя (без символа '@' или с ним) с полученными в ответе.
     *
     * @param username Имя пользователя, например "Isochrone" или "@Isochrone".
     * @return Числовой chat_id в виде строки, если найден; иначе null.
     */
    private String getChatIdByUsername(String username) {
        // Если имя начинается с '@', удаляем его для сравнения
        if (username.startsWith("@")) {
            username = username.substring(1);
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Запрос к методу getUpdates
            HttpGet request = new HttpGet(getUpdatesUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Читаем ответ целиком
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent(), StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String jsonResponse = sb.toString();
                    // Парсим JSON-ответ с помощью Gson
                    Gson gson = new Gson();
                    GetUpdatesResponse updatesResponse = gson.fromJson(jsonResponse, GetUpdatesResponse.class);
                    if (updatesResponse != null && updatesResponse.ok && updatesResponse.result != null) {
                        // Проходим по каждому update и ищем нужное имя пользователя (from.username или chat.username)
                        for (Update update : updatesResponse.result) {
                            if (update.message != null && update.message.from != null) {
                                String fromUsername = update.message.from.username;
                                if (fromUsername != null && fromUsername.equalsIgnoreCase(username)) {
                                    // Если найдено совпадение, возвращаем числовой идентификатор чата, как строку
                                    return String.valueOf(update.message.chat.id);
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Ошибка getUpdates: HTTP статус " + statusCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Если подходящий chat_id не найден, возвращаем null
        return null;
    }

    @Override
    public void sendCode(String destination, String code) {
        // Проверяем, что destination не пустой
        if (destination == null || destination.trim().isEmpty()) {
            System.out.println("Ошибка: не указан идентификатор чата.");
            return;
        }

        // Если destination не начинается с "@", добавляем его (такой формат ожидается для username)
        if (!destination.startsWith("@")) {
            destination = "@" + destination;
        }

        // Используем метод getChatIdByUsername для получения числового chat_id по имени пользователя
        String resolvedChatId = getChatIdByUsername(destination);
        if (resolvedChatId == null) {
            System.out.println("Ошибка: не удалось получить chat_id для пользователя " + destination
                    + ". Убедитесь, что пользователь начал диалог с ботом.");
            return;
        }

        // Формируем сообщение для отправки
        String message = destination + ", ваш код подтверждения: " + code;
        try {
            String url = telegramApiUrl + "?chat_id=" + URLEncoder.encode(resolvedChatId, StandardCharsets.UTF_8.toString())
                    + "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet sendRequest = new HttpGet(url);
                try (CloseableHttpResponse sendResponse = httpClient.execute(sendRequest)) {
                    if (sendResponse.getStatusLine().getStatusCode() == 200) {
                        System.out.println("Сообщение в Telegram отправлено успешно");
                    } else {
                        System.out.println("Ошибка при отправке в Telegram");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Классы для парсинга JSON-ответа от метода getUpdates

    private static class GetUpdatesResponse {
        boolean ok;
        Update[] result;
    }

    private static class Update {
        @SerializedName("update_id")
        long updateId;
        Message message;
    }

    private static class Message {
        @SerializedName("message_id")
        long messageId;
        From from;
        Chat chat;
        long date;
        String text;
    }

    private static class From {
        long id;
        @SerializedName("is_bot")
        boolean isBot;
        @SerializedName("first_name")
        String firstName;
        String username;
        @SerializedName("language_code")
        String languageCode;
    }

    private static class Chat {
        long id;
        @SerializedName("first_name")
        String firstName;
        String username;
        String type;
    }
}

