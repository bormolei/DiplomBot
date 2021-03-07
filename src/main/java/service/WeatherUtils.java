package service;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-хранилище иконок
 */
public class WeatherUtils {
    public final static Map<String, String> weatherIconsCodes = new HashMap<>();

    static {
        weatherIconsCodes.put("Clear", "☀");
        weatherIconsCodes.put("Rain", "☔");
        weatherIconsCodes.put("Snow", "❄");
        weatherIconsCodes.put("Clouds", "☁");
    }
}
