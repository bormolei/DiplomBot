package service.weather;

import Exceptions.Calendar.MonthException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.telegram.TelegramKeyboard;
import service.translate.Translator;
import utils.WeatherUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherBot implements WeatherParser {
    private final static String API_CALL_TEMPLATE = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private final static String API_KEY_TEMPLATE = "&units=metric&cnt=40&APPID=af2ed85eb4017a81d8584d861a45f21a";
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static Locale locale = new Locale("ru");
    private final static DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("LLLL-dd HH:mm", locale);

    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    @Override
    public String getReadyForecast(String city, int days) {
        String result;
        try {
            String jsonRawData = downloadJsonRawData(city);
            List<String> linesOfForecast = convertRawDataToList(jsonRawData, days);
            result = String.format("%s:%s%s", city, System.lineSeparator(), parseForecastDataFromList(linesOfForecast));
        } catch (IllegalArgumentException e) {
            return String.format("Данный город \"%s\" не найден. Или вам необходимо сменить режим", city);
        } catch (Exception e) {
            e.printStackTrace();
            return "Сервис сейчас не работает, пожалуйста попробуйте позже";
        }
        return result;
    }

    private static String downloadJsonRawData(String city) throws Exception {
        String engCity = Translator.downloadJsonRawData(city);
        String urlString = API_CALL_TEMPLATE + engCity + API_KEY_TEMPLATE;
        URL urlObject = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static List<String> convertRawDataToList(String data, int days) throws Exception {
        List<String> weatherList = new ArrayList<>();
        JsonNode arrNode = new ObjectMapper().readTree(data).get("list");
        int date;

        if (arrNode.isArray()) {
            switch (days) {
                case 1:
                    date = LocalDateTime.now().getDayOfMonth();

                    for (final JsonNode objNode : arrNode) {
                        String forecastTime = objNode.get("dt_txt").toString().replace("\"", "");
                        SimpleDateFormat format = new SimpleDateFormat();
                        format.applyPattern("yyyy-MM-dd HH:mm:ss");
                        int month = new Timestamp(format.parse(forecastTime).getTime()).toLocalDateTime().getDayOfMonth();

                        if (month == date) {
                            weatherList.add(objNode.toString());
                        }
                    }
                    break;
                case 3:
                    for (int i = 0; i < days; i++) {
                        date = LocalDateTime.now().plusDays(i).getDayOfMonth();

                        for (final JsonNode objNode : arrNode) {
                            String forecastTime = objNode.get("dt_txt").toString().replace("\"", "");
                            SimpleDateFormat format = new SimpleDateFormat();
                            format.applyPattern("yyyy-MM-dd HH:mm:ss");
                            int month = new Timestamp(format.parse(forecastTime).getTime()).toLocalDateTime().getDayOfMonth();
                            if (month == date
                                    && (forecastTime.contains("06:00") || forecastTime.contains("12:00") || forecastTime.contains("18:00"))) {
                                weatherList.add(objNode.toString());
                            }
                        }
                    }

                    break;
                case 5:
                    for (int i = 0; i < days; i++) {
                        date = LocalDateTime.now().plusDays(i).getDayOfMonth();

                        for (final JsonNode objNode : arrNode) {
                            String forecastTime = objNode.get("dt_txt").toString().replace("\"", "");

                            SimpleDateFormat format = new SimpleDateFormat();
                            format.applyPattern("yyyy-MM-dd HH:mm:ss");
                            int month = new Timestamp(format.parse(forecastTime).getTime()).toLocalDateTime().getDayOfMonth();

                            if (month == date
                                    && (forecastTime.contains("09:00") || forecastTime.contains("18:00"))) {
                                weatherList.add(objNode.toString());
                            }
                        }
                    }
                    break;
            }
        }
        return weatherList;
    }

    private static String parseForecastDataFromList(List<String> weatherList) throws Exception {
        final StringBuilder sb = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String line : weatherList) {
            {
                String dateTime;
                JsonNode mainNode;
                JsonNode weatherArrNode;
                try {
                    mainNode = objectMapper.readTree(line).get("main");
                    weatherArrNode = objectMapper.readTree(line).get("weather");
                    for (final JsonNode objNode : weatherArrNode) {
                        dateTime = objectMapper.readTree(line).get("dt_txt").toString();
                        sb.append(formatForecastData(dateTime, objNode.get("main").toString(), mainNode.get("temp").asDouble()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private static String formatForecastData(String dateTime, String description, double temperature) throws Exception {
        LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), INPUT_DATE_TIME_FORMAT);
        String formattedDateTime = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT);

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + (Math.round(temperature));
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }

        String formattedDescription = description.replaceAll("\"", "");
        String weatherDescription = WeatherUtils.weatherNames.get(formattedDescription);
        String weatherIconCode = WeatherUtils.weatherIconsCodes.get(formattedDescription);
        return String.format("%s   %s %s %s%s", formattedDateTime, formattedTemperature, weatherDescription, weatherIconCode, System.lineSeparator());
    }


    public static ReplyKeyboard createHours(int day) throws MonthException {
        TelegramKeyboard.clearKeyBoard();
        rowList.clear();
        return WeatherMethods.createHoursKeyBoard(rowList, day);
    }
}
