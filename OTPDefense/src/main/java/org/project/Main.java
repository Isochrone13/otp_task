package org.project;

import org.project.dao.SchemaInitializer;
import org.project.model.User;
import org.project.service.AuthService;
import org.project.service.OTPService;
import org.project.service.RegistrationService;
import org.project.service.ExpirationTask;
import org.project.service.AdminService;
import org.project.api.HttpApiServer;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class Main {

    // Создаём экземпляры сервисов, которые будут использоваться во всем приложении
    private static RegistrationService registrationService = new RegistrationService();
    private static AuthService authService = new AuthService();
    private static OTPService otpService = new OTPService();

    public static void main(String[] args) {

        // Инициализируем схему базы данных: создаём таблицы и заполняем otp_configuration,
        // если таблицы отсутствуют или конфигурация пуста.
        SchemaInitializer.init();

        // Запускаем HTTP API сервер в отдельном потоке, чтобы он не блокировал работу консольного меню
        new Thread(() -> {
            HttpApiServer apiServer = new HttpApiServer();
            try {
                apiServer.start(8080); // Запускаем сервер на порту 8080
            } catch (IOException e) {
                // Если произошла ошибка при запуске, выводим сообщение об ошибке
                System.err.println("Ошибка запуска HTTP API сервера: " + e.getMessage());
            }
        }).start();

        // Небольшая пауза, чтобы лог "HTTP API сервер запущен..." успел показаться
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Используем ScheduledExecutorService для проверки просроченных OTP-кодов каждые 60 секунд
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new ExpirationTask(), 0, 60, TimeUnit.SECONDS);

        // Создаём объект Scanner для чтения ввода пользователя из консоли
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в OTP-сервис");

        // Основной цикл работы консольного меню
        while (true) {
            // Отображаем главное меню приложения
            System.out.println("\n1. Регистрация");
            System.out.println("2. Логин");
            System.out.println("3. Выход");
            System.out.print("Выберите действие: ");
            int choice;
            // Обрабатываем ввод, чтобы не падало при некорректном формате (например, буквы)
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число!");
                continue; // Переходим к следующей итерации, если ввод не является числом
            }

            if (choice == 1) {
                // Регистрируем пользователя
                System.out.print("Введите логин: ");
                String login = scanner.nextLine();
                System.out.print("Введите пароль: ");
                String password = scanner.nextLine();

                // Запрос выбора роли (ADMIN или USER)
                int roleOption = 0;
                boolean validRole = false;
                while (!validRole) {
                    System.out.println("Выберите роль:");
                    System.out.println("1. ADMIN");
                    System.out.println("2. USER");
                    System.out.print("Введите цифру: ");
                    String roleStr = scanner.nextLine();
                    try {
                        roleOption = Integer.parseInt(roleStr);
                        if (roleOption == 1 || roleOption == 2) {
                            validRole = true; // Корректный выбор, выходим из цикла
                        } else {
                            System.out.println("Ошибка: введите число 1 или 2.");
                        }
                    } catch (NumberFormatException e) {
                        // Обработка ошибки, если пользователь ввёл не число
                        System.out.println("Ошибка: введите корректное число.");
                    }
                }
                // Определяем строковое значение роли по выбору пользователя
                String role = (roleOption == 1) ? "ADMIN" : "USER";

                // Вызываем сервис регистрации. Если регистрация успешна, уведомляем пользователя, иначе – выводим ошибку.
                if (registrationService.registerUser(login, password, role)) {
                    System.out.println("Регистрация успешна.");
                } else {
                    System.out.println("Ошибка регистрации.");
                }
            } else if (choice == 2) {
                // Логин пользователя
                System.out.print("Введите логин: ");
                String login = scanner.nextLine();
                System.out.print("Введите пароль: ");
                String password = scanner.nextLine();
                // Получаем токен, если логин прошёл успешно
                String token = authService.login(login, password);
                if (token != null) {
                    System.out.println("Логин успешен. Токен: " + token);
                    // Получаем объект User по токену для определения его роли
                    User user = authService.getUserByToken(token);
                    // Если роль пользователя ADMIN, открываем админское меню, иначе – пользовательское меню
                    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        adminMenu(scanner);
                    } else {
                        userMenu(scanner);
                    }
                } else {
                    System.out.println("Неверные данные для входа.");
                }
            } else if (choice == 3) {
                // Выход из приложения: закрываем планировщик и прерываем основной цикл
                System.out.println("Выход...");
                scheduler.shutdown();
                break;
            }
        }
        // Закрываем Scanner после завершения работы программы
        scanner.close();
    }

    // Метод для работы админского меню
    private static void adminMenu(Scanner scanner) {
        // Создаём экземпляр AdminService для управления действиями администратора
        AdminService adminService = new AdminService();

        while (true) {
            // Отображаем меню администратора
            System.out.println("\n--- Меню Администратора ---");
            System.out.println("1. Изменить конфигурацию OTP");
            System.out.println("2. Список пользователей");
            System.out.println("3. Удалить пользователя");
            System.out.println("4. Выход");
            System.out.print("Выберите действие: ");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                // Изменение конфигурации OTP
                System.out.print("Введите новую длину OTP-кода: ");
                int newLength = Integer.parseInt(scanner.nextLine());
                System.out.print("Введите новое время жизни OTP (в секундах): ");
                int newDuration = Integer.parseInt(scanner.nextLine());

                // Вызываем метод изменения конфигурации и уведомляем администратора об результате
                if (adminService.changeOtpConfiguration(newLength, newDuration)) {
                    System.out.println("Конфигурация OTP успешно изменена.");
                } else {
                    System.out.println("Ошибка изменения конфигурации OTP.");
                }
            } else if (choice == 2) {
                // Вывод списка пользователей (кроме администраторов)
                System.out.println("Список пользователей:");
                for (User user : adminService.listNonAdminUsers()) {
                    System.out.println("ID: " + user.getId() + " | Логин: " + user.getLogin());
                }
            } else if (choice == 3) {
                // Удаление пользователя по ID
                System.out.print("Введите ID пользователя для удаления: ");
                int userId = Integer.parseInt(scanner.nextLine());
                if (adminService.deleteUserById(userId)) {
                    System.out.println("Пользователь успешно удален.");
                } else {
                    System.out.println("Ошибка удаления пользователя. Проверьте правильность ID.");
                }
            } else if (choice == 4) {
                // Выход из админского меню
                break;
            } else {
                System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }

    // Метод для работы пользовательского меню
    private static void userMenu(Scanner scanner) {
        while (true) {
            // Отображаем меню для обычного пользователя
            System.out.println("\n--- Меню пользователя ---");
            System.out.println("1. Сгенерировать OTP");
            System.out.println("2. Валидировать OTP");
            System.out.println("3. Выход");
            System.out.print("Выберите действие: ");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                // Генерация OTP-кода: запрашиваем идентификатор операции
                System.out.print("Введите идентификатор операции: ");
                String opId = scanner.nextLine();

                int sendOption = 0;
                boolean validOption = false;
                // Запрашиваем выбор способа отправки OTP-кода с проверкой корректности ввода
                while (!validOption) {
                    System.out.println("Выберите способ получения OTP-кода:");
                    System.out.println("1. Отправить по e-mail");
                    System.out.println("2. Отправить по SMS");
                    System.out.println("3. Отправить в Telegram");
                    System.out.println("4. Сохранить в файл");
                    System.out.print("Введите цифру: ");
                    String optionStr = scanner.nextLine();
                    try {
                        sendOption = Integer.parseInt(optionStr);
                        if (sendOption >= 1 && sendOption <= 4) {
                            validOption = true;
                        } else {
                            System.out.println("Ошибка: введите число от 1 до 4.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите корректное число.");
                    }
                }

                String destination = "";
                // Если выбран способ, требующий адреса (не вариант "сохранить в файл"), запрашиваем его
                if (sendOption != 4) {
                    System.out.print("Введите адрес для выбранного способа получения: ");
                    destination = scanner.nextLine();
                }

                // Генерируем OTP с выбранными параметрами и выводим код
                String otp = otpService.generateOTP(opId, sendOption, destination);
                System.out.println("Сгенерированный OTP: " + otp);
            } else if (choice == 2) {
                // Валидируем OTP-код: запрашиваем идентификатор операции и сам код
                System.out.print("Введите идентификатор операции: ");
                String opId = scanner.nextLine();
                System.out.print("Введите OTP: ");
                String otp = scanner.nextLine();
                if (otpService.validateOTP(opId, otp)) {
                    System.out.println("OTP успешно подтверждён.");
                } else {
                    System.out.println("Ошибка подтверждения OTP.");
                }
            } else if (choice == 3) {
                // Выход из пользовательского меню
                break;
            }
        }
    }
}
