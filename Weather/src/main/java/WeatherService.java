// Импортируем необходимые классы
import java.net.http.HttpClient; // для отправки/получения http запросов
import java.net.http.HttpRequest; // для формирования http запроса
import java.net.http.HttpResponse; // для формирования http ответа
import java.net.URI; // 'URL(java.lang.String)' deprecated поэтому используем более новую библиотеку
import java.util.Scanner; // для считывания пользовательского ввода

// Добавляем сторонние библиотеки для работы с JSON
import org.json.JSONObject;
import org.json.JSONArray;

public class WeatherService {
    public static void main(String[] args) {
        // Объявляем константу для хранения API-ключа
        final String API_KEY = "INSERT API KEY HERE"
        // Создаем объект Scanner для ввода данных с консоли
        Scanner scanner = new Scanner(System.in);
        // Флаг успешного выполнения
        boolean success = false;
        // Создаём экземпляр HttpClient, для отправки HTTP-запросов и получения HTTP-ответов
        HttpClient client = HttpClient.newHttpClient();

        // Выполняем пока не получим успешный ответ от сервера
        while (!success) {
            try {
                // Запрашиваем у пользователя широту и считываем ввод
                System.out.print("Введите широту (lat): ");
                String lat = scanner.nextLine();
                // Запрашиваем у пользователя долготу и считываем ввод
                System.out.print("Введите долготу (lon): ");
                String lon = scanner.nextLine();
                // Запрашиваем у пользователя количество дней по которым нужны данные
                // Для справки, в тарифе «Тестовый» максимально допустимое значение limit — 11,
                // при этом больше 7 дней Yandex API не выдаёт
                System.out.print("Введите количество дней: ");
                int limit = Integer.parseInt(scanner.nextLine());

                // Формируем URL с параметрами lat, lon и limit
                String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon + "&limit=" + limit;
                URI uri = URI.create(urlString);

                // Создаём объект запроса с параметрами: URL + API ключ + тип запроса
                HttpRequest request = HttpRequest.newBuilder().uri(uri).header("X-Yandex-API-Key", API_KEY).GET().build();
                // Создаём объект ответа и получаем строку
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Если ответ успешный, то входим в обработку полученных данных
                if (response.statusCode() == 200) {
                    // Выводим на экран весь ответ в формате JSON
                    String responseBody = response.body();
                    System.out.println("\nПолный ответ от сервиса:");
                    System.out.println(responseBody);

                    // Парсим JSON-ответ, создавая объект JSONObject из строки ответа
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    // Извлекаем часовой пояс (чтобы было понятнее, где местоположение)
                    String timezone = jsonResponse.getJSONObject("info").getJSONObject("tzinfo").getString("name");
                    System.out.println("\nЧасовой пояс: " + timezone);
                    // Извлекаем текущую температуру fact {temp} и выводим на экран
                    int currentTemp = jsonResponse.getJSONObject("fact").getInt("temp");
                    System.out.println("\nТекущая температура: " + currentTemp + "°C");

                    // Извлекаем массив прогнозов из JSON-ответа
                    JSONArray forecasts = jsonResponse.getJSONArray("forecasts");
                    int sumTemp = 0; // переменная для суммы температур
                    int count = 0; // переменная для подсчета количества дней

                    // Проходимся по каждому дню прогноза в массиве
                    for (int i = 0; i < forecasts.length(); i++) {
                        // Т.к. day – агрегированный прогноз на день, получаем среднюю
                        // температуру дня по ключу "temp_avg" в объекте forecasts
                        int dayTempAvg = forecasts.getJSONObject(i).getJSONObject("parts")
                                .getJSONObject("day")
                                .getInt("temp_avg");
                        sumTemp += dayTempAvg; // добавляем среднюю температуру дня к общей сумме
                        count++; // увеличиваем счетчик дней
                    }
                    // Вычисляем среднюю температуру за заданный период
                    double avgTemp = (double) sumTemp / count;
                    System.out.println(String.format("Средняя температура за период (%d дней): %.3f°C", count, avgTemp));
                    success = true; // Успешное выполнение, выходим из цикла
                } else { // Если ответ содержит ошибку
                    System.out.println("\nНе удалось получить данные от сервера.");
                    System.out.println("Код ответа: " + response.statusCode());
                    System.out.println("Попробуйте ввести данные снова.\n");
                }

            } catch (Exception e) {
                e.printStackTrace(); // в случае ошибки выводим стек трейс для отладки
            }
        }
    }
}