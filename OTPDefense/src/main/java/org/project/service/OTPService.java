package org.project.service;

import org.project.dao.OtpDao;
import org.project.dao.OtpDaoConfig;
import org.project.model.OTPConfig;
import org.project.model.OTPStatus;
import org.project.model.OTP;
import java.time.LocalDateTime;
import java.util.Random;

public class OTPService {
    // Создаём объект для работы с OTP-кодами (создание, поиск, обновление записей)
    private OtpDao otpDao = new OtpDao();

    // Создаём объект для работы с конфигурацией OTP (например, длина кода, время жизни)
    private OtpDaoConfig otpDaoConfig = new OtpDaoConfig();

    // Создаём экземпляры сервисов уведомлений
    private EmailNotificationService emailService = new EmailNotificationService();
    private SmsNotificationService smsService = new SmsNotificationService();
    private TelegramNotificationService telegramService = new TelegramNotificationService();
    private FileNotificationService fileService = new FileNotificationService();

    // Метод для генерации OTP-кода для заданной операции
    public String generateOTP(String operationId, int sendMethod, String destination) {
        // Получаем текущую конфигурацию OTP из базы данных. Если конфигурация не найдена, используем значения по умолчанию.
        OTPConfig config = otpDaoConfig.getConfiguration();
        int length = (config != null) ? config.getCodeLength() : 6;  // Длина кода: либо из конфигурации, либо 6 символов
        int validSeconds = (config != null) ? config.getValidDurationSeconds() : 300;  // Время жизни: либо из конфигурации, либо 300 секунд

        // Генерируем случайный OTP-код заданной длины
        String code = generateRandomCode(length);

        // Создаем объект OTP и заполняем его данными
        OTP otp = new OTP();
        otp.setOperationId(operationId);            // Привязываем OTP к операции
        otp.setCode(code);                          // Устанавливаем сгенерированный код
        otp.setStatus(OTPStatus.ACTIVE);            // Устанавливаем статус как ACTIVE
        otp.setCreationTime(LocalDateTime.now());   // Время создания устанавливаем в текущий момент
        otp.setExpirationTime(LocalDateTime.now().plusSeconds(validSeconds));  // Рассчитываем время истечения кода

        // Если запись OTP успешно сохранена в базе
        if (otpDao.createOTPCode(otp)) {
            // Выбор способа отправки OTP-кода в зависимости от параметра sendMethod
            switch(sendMethod) {
                case 1:
                    // Отправка OTP по e-mail
                    emailService.sendCode(destination, code);
                    break;
                case 2:
                    // Отправка OTP через SMS
                    smsService.sendCode(destination, code);
                    break;
                case 3:
                    // Отправка OTP через Telegram
                    telegramService.sendCode(destination, code);
                    break;
                case 4:
                    // Сохранение OTP в файл (destination не требуется)
                    fileService.sendCode("", code);
                    break;
                default:
                    // Если введен неверный вариант, выводим сообщение об ошибке
                    System.out.println("Неверный выбор метода отправки.");
            }
            // Возвращаем сгенерированный OTP-код
            return code;
        }
        // Если не удалось сохранить OTP-код в базе, возвращаем null
        return null;
    }

    // Метод для проверки OTP-кода
    public boolean validateOTP(String operationId, String code) {
        // Ищем в базе запись OTP с заданным operationId и кодом, причем статус должен быть ACTIVE
        OTP otp = otpDao.getOTPCode(operationId, code);
        if(otp != null) {
            // Если время истечения OTP больше текущего времени, код действителен
            if(otp.getExpirationTime().isAfter(LocalDateTime.now())) {
                // Обновляем статус OTP на USED и возвращаем true (код успешно подтверждён)
                otpDao.updateOTPStatus(otp.getId(), OTPStatus.USED);
                return true;
            } else {
                // Если код устарел, обновляем статус на EXPIRED
                otpDao.updateOTPStatus(otp.getId(), OTPStatus.EXPIRED);
            }
        }
        // Если OTP не найден или он истек, возвращаем false
        return false;
    }

    // Метод для генерации случайного цифрового OTP-кода заданной длины
    private String generateRandomCode(int length) {
        Random random = new Random();       // Объект Random для генерации случайных чисел
        StringBuilder sb = new StringBuilder(); // StringBuilder для построения строки кода
        for(int i = 0; i < length; i++){
            // Каждая цифра генерируется случайным числом от 0 до 9
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
