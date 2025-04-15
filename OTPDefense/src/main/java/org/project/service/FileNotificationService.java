package org.project.service;

import java.io.FileWriter;
import java.io.IOException;

public class FileNotificationService implements NotificationService {
    @Override
    public void sendCode(String destination, String code) {
        // Открываем (или создаём, если не существует) файл "otp_codes.txt" для записи
        try (FileWriter writer = new FileWriter("otp_codes.txt", true)) {
            // Записываем в файл информацию: куда должен быть отправлен код и сам код
            writer.write("Destination: " + destination + " | Code: " + code + "\n");
            System.out.println("OTP сохранён в файл.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
