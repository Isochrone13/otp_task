package org.project.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.project.service.RegistrationService;
import org.project.service.AuthService;
import org.project.service.OTPService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpApiServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpApiServer.class);
    private HttpServer server;
    private Gson gson = new Gson();

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/register", new ApiRegisterHandler());
        server.createContext("/api/login", new ApiLoginHandler());
        server.createContext("/api/otp/generate", new ApiOtpGenerateHandler());
        server.createContext("/api/otp/validate", new ApiOtpValidateHandler());
        server.setExecutor(null); // использование дефолтного executor-а
        server.start();
        logger.info("HTTP API сервер запущен на порту " + port);
    }

    public void stop() {
        server.stop(0);
        logger.info("HTTP API сервер остановлен");
    }

    // Обработчик регистрации
    static class ApiRegisterHandler implements HttpHandler {
        private RegistrationService registrationService = new RegistrationService();
        private Gson gson = new Gson();

        static class RegisterRequest {
            String login;
            String password;
            int roleOption; // 1 для ADMIN, 2 для USER
        }
        static class ApiResponse {
            String message;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                RegisterRequest request = gson.fromJson(isr, RegisterRequest.class);
                String role = (request.roleOption == 1) ? "ADMIN" : "USER";
                boolean success = registrationService.registerUser(request.login, request.password, role);
                ApiResponse response = new ApiResponse();
                if (success) {
                    response.message = "Регистрация успешна.";
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    response.message = "Ошибка регистрации (возможно, ADMIN уже существует).";
                    exchange.sendResponseHeaders(400, 0);
                }
                String jsonResponse = gson.toJson(response);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
            } catch(Exception ex) {
                logger.error("Ошибка при регистрации: " + ex.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    // Обработчик логина
    static class ApiLoginHandler implements HttpHandler {
        private AuthService authService = new AuthService();
        private Gson gson = new Gson();

        static class LoginRequest {
            String login;
            String password;
        }
        static class LoginResponse {
            String token;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                LoginRequest request = gson.fromJson(isr, LoginRequest.class);
                String token = authService.login(request.login, request.password);
                LoginResponse response = new LoginResponse();
                if (token != null) {
                    response.token = token;
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    response.token = "";
                    exchange.sendResponseHeaders(401, 0);
                }
                String jsonResponse = gson.toJson(response);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
            } catch(Exception ex) {
                logger.error("Ошибка при логине: " + ex.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    // Обработчик генерации OTP
    static class ApiOtpGenerateHandler implements HttpHandler {
        private OTPService otpService = new OTPService();
        private Gson gson = new Gson();

        static class OtpGenerateRequest {
            String operationId;
            int sendOption; // 1 - Email, 2 - SMS, 3 - Telegram, 4 - Сохранить в файл
            String destination; // используется, если sendOption != 4
        }
        static class OtpGenerateResponse {
            String otp;
            String message;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                OtpGenerateRequest request = gson.fromJson(isr, OtpGenerateRequest.class);
                String otp = otpService.generateOTP(request.operationId, request.sendOption, request.destination);
                OtpGenerateResponse response = new OtpGenerateResponse();
                if (otp != null) {
                    response.otp = otp;
                    response.message = "OTP сгенерирован успешно.";
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    response.otp = "";
                    response.message = "Ошибка генерации OTP.";
                    exchange.sendResponseHeaders(400, 0);
                }
                String jsonResponse = gson.toJson(response);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
            } catch(Exception ex) {
                logger.error("Ошибка генерации OTP: " + ex.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    // Обработчик проверки OTP
    static class ApiOtpValidateHandler implements HttpHandler {
        private OTPService otpService = new OTPService();
        private Gson gson = new Gson();

        static class OtpValidateRequest {
            String operationId;
            String otp;
        }
        static class OtpValidateResponse {
            boolean valid;
            String message;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                OtpValidateRequest request = gson.fromJson(isr, OtpValidateRequest.class);
                boolean valid = otpService.validateOTP(request.operationId, request.otp);
                OtpValidateResponse response = new OtpValidateResponse();
                response.valid = valid;
                response.message = valid ? "OTP корректен." : "OTP некорректен или истек.";
                exchange.sendResponseHeaders(valid ? 200 : 400, 0);
                String jsonResponse = gson.toJson(response);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
                }
            } catch(Exception ex) {
                logger.error("Ошибка проверки OTP: " + ex.getMessage());
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }
}
