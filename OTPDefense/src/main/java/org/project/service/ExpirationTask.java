package org.project.service;

import org.project.dao.OtpDao;
import org.project.model.OTP;
import org.project.model.OTPStatus;
import java.time.LocalDateTime;
import java.util.List;

public class ExpirationTask implements Runnable {
    // Создаём объект для работы с OTP-кодами в базе
    private OtpDao otpCodeDA = new OtpDao();

    @Override
    public void run() {
        // Получаем список всех активных OTP-кодов
        List<OTP> activeOtps = otpCodeDA.getActiveOTPCodes();
        // Проходим по каждому OTP
        for (OTP otp : activeOtps) {
            // Если время истечения OTP меньше текущего времени, значит код просрочен
            if (otp.getExpirationTime().isBefore(LocalDateTime.now())) {
                // Обновляем статус OTP на EXPIRED в базе данных
                otpCodeDA.updateOTPStatus(otp.getId(), OTPStatus.EXPIRED);
                // Выводим сообщение о том, что код просрочен
                System.out.println("OTP " + otp.getCode() + " просрочен.");
            }
        }
    }
}

