# OTP-сервис

OTP-сервис – это система для защиты операций с помощью временных одноразовых кодов (OTP). Сервис позволяет:
- Регистрировать пользователей (в роли ADMIN или USER)
- Аутентифицировать пользователей и выдавать им токены
- Генерировать OTP-коды для подтверждения операций с возможностью их отправки через:
  - Email (с использованием JavaMail)
  - SMS (эмуляция через SMPP)
  - Telegram (с использованием Telegram API)
  - Сохранение в файл (otp_codes.txt)
- Валидировать введённые OTP-коды
- Администратор имеет собственные функции:
  - Изменять конфигурацию OTP (длина кода, время жизни)
  - Просматривать список пользователей (без администраторов)
  - Удалять пользователей

## Структура проекта

Архитектура проекта включает в себя следующие слои:
- **Model:** Классы-сущности (User, OTP, OTPConfig, OTPStatus)
- **DAO:** Доступ к базе данных PostgreSQL через JDBC (DbUtil, UserDao, OtpDao, OtpDaoConfig, SchemaInitializer)
- **Service:** Бизнес-логика (RegistrationService, AuthService, OTPService, AdminService, Notification-сервисы, ExpirationTask)
- **API:** HTTP-сервер на базе `com.sun.net.httpserver`, предоставляющий REST‑эндпоинты для регистрации, логина, генерации и проверки OTP.

## Как пользоваться сервисом

При запуске приложения (из консоли) запускаются:
- HTTP API сервер (на порту 8080) для обработки запросов.
- Периодическая задача проверки просроченных OTP-кодов.
- Основное консольное меню, где доступно следующее:

### Главное меню
1. **Регистрация.** В этом случае требуется: 
   - Ввод логина.
   - Ввод пароля.
   - Выбор роли (система допускает только одного администратора).

2. **Логин:** В этом случае требуется: 
   - Ввод логина.
   - Ввод пароля.  
   При успешном входе отображается токен, и далее открывается:
   - **Меню администратора** (если роль ADMIN) в котором можно:
     1. Изменить конфигурацию OTP (можно выбрать новую длину и время жизни кода).
     2. Запросить список пользователей (выводит ID и логин пользователей без администраторов).
     3. Удалить пользователя (требуется ID пользователя для удаления).
     4. Выход из меню администратора.
   - **Пользовательское меню** (если роль USER) в котором можно:
     1. Сгенерировать OTP. Потребуется ввести идентификатор операции, выбрать способ получения OTP (1: e-mail, 2: SMS, 3: Telegram, 4: Сохранение в файл) и адрес для получения (если выбран способ с адресом). Для демонстрационных целей приложение предлагает вводить адрес при каждом запросе OTP, но эти данные логичнее хранить в базе данных
     2. Валидировать OTP. Потребуется ввести идентификатор операции и OTP-код.
     3. Выйти из пользовательского меню.

3. **Выход:**  
   Завершает работу приложения.

### HTTP API эндпоинты (на порту 8080):
- **POST /api/register:**  
  Принимает JSON с параметрами (1 для ADMIN, 2 для USER):  
  ```json
  {
    "login": "username",
    "password": "userpassword",
    "roleOption": 1 
  }
  ```
  - **POST /api/otp/generate:**  
  Принимает JSON с параметрами:  
  ```json
  {
    "login": "username",
    "password": "userpassword"
  }
  ```
  - **POST /api/otp/validate:**  
  Принимает JSON с параметрами (1: Email, 2: SMS, 3: Telegram, 4: Сохранение в файл):  
  ```json
  {
    "operationId": "operation123",
    "sendOption": 1,
    "destination": "email@example.com"
  }
  ```

## Особенности работы разных вариантов отправки
1. **Отправка с помощью email**

В проекте в папке src/main/resources находится файл email.properties. Этот файл содержит параметры конфигурации для SMTP подключения к почтовому сервису.

2. **Отправка с помощью SMS**

Для отправки СМС настроена интеграция с эмулятором SMPP сервера в Java. После ввода номера телефона для отправки СМС, отправка СМС эмулируется и в консоль выводится сообщение о том, что СМС отправлено.

3. **Отправка с помощью Telegram**  

Для реализации отправки сообщения с помощью телеграм используются возможности api telegram. В проекте в папке src/main/resources находится файл telegram.properties для проекта. В этом файле хранится api токен используемого телеграм бота. Логика работы программы реализована таким образом, что когда пользователь начинает диалог с ботом (api токен которого указан в telegram.properties), программа направляет запрос
```html
https://api.telegram.org/botYOUR_BOT_TOKEN/getUpdates
```

В результате программа получает примерно следующий ответ:
```json
{
    "ok": true,
    "result": [
        {
            "update_id": 123456789,
            "message": {
                "message_id": 1,
                "from": {
                    "id": 987654321,
                    "is_bot": false,
                    "first_name": "YourName",
                    "username": "YourUsername",
                    "language_code": "en"
                },
                "chat": {
                    "id": -123456789,
                    "first_name": "YourName",
                    "username": "YourUsername",
                    "type": "private"
                },
                "date": 1610000000,
                "text": "Hello, bot!"
            }
        }
    ]
}
```
Из этого сообщения программа парсит id чата того пользователя, который был указан как получатель сообщения с OTP кодом. Т.к. этот запрос возвращает относительно свежие сообщения боту, если пользователь давно запускал бота и не писал боту сообщений после запуска, то программа не сможет получить id чата для отправки сообщения. Для полноценной реализации необходимо было бы отдельно сохранять в базе данных id чатов и пользователей как только они запускают бота, но в данном проекте данный функционал не реализован.

## Как протестировать код

1. **Скачать проект с Github.**  
2. **Открыть проект в IDE.**
3. **Заполнить файлы email.properties данными SMTP почтового сервера и telegram.properties данными бота (api токен).**
4. **Заполнить необходимыми данными класс DbUtil для подключения к PostgreSQL базе данных**. По умолчанию указано локальное подключение  "jdbc:postgresql://localhost:5432/postgres". Также нужно заполнить имя пользователя и пароль. В программе реализована автоматическая инициализация необходимых таблиц.
5. **Запустить диалог с ботом в телеграм.**
6. **Запустить программу через ide в классе main и следовать указаниям в консоли.**

## Как протестировать api

После запуска программы через ide в классе main должен запуститься локальный http сервер, который будет принимать api запросы к приложению. Для проверки на Windows можно отправлять следующие curl запросы:

1. **Регистрация:**
 ```powershell
curl.exe -X POST http://localhost:8080/api/register -H "Content-Type: application/json" -d '{\"login\":\"testUser\",\"password\":\"pass123\",\"roleOption\":2}'
```
2. **Логин:**
 ```powershell
curl.exe -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d '{\"login\":\"testUser\",\"password\":\"pass123\"}'
```
3. **Генерация OTP (например, через Email):**
 ```powershell
curl.exe -X POST http://localhost:8080/api/otp/generate -H "Content-Type: application/json" -d '{\"operationId\":\"op123\",\"sendOption\":1,\"destination\":\"test@example.com\"}'
```
4. **Валидация OTP:**
 ```powershell
curl.exe -X POST http://localhost:8080/api/otp/validate -H "Content-Type: application/json" -d '{\"operationId\":\"op123\",\"otp\":\"123456\"}'
 ```
