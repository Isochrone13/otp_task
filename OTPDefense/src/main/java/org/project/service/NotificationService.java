package org.project.service;

// Интерфейс NotificationService определяет общий метод для всех сервисов рассылки OTP.
public interface NotificationService {
    // Метод sendCode принимает два параметра:
    // destination — строка с адресом получателя; code — сгенерированный OTP-код
    void sendCode(String destination, String code);
}

