package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-хранилище иконок
 */
public class WeatherUtils {
    public final static Map<String, String> weatherIconsCodes = new HashMap<>();
    public final static Map<String, String> weatherNames = new HashMap<>();

    static {
        weatherIconsCodes.put("Clear", "☀");
        weatherIconsCodes.put("Rain", "☔");
        weatherIconsCodes.put("Snow", "❄");
        weatherIconsCodes.put("Clouds", "☁");
    }

    static {
        weatherNames.put("Clear", "Ясно");
        weatherNames.put("Rain", "Дождь");
        weatherNames.put("Snow", "Снег");
        weatherNames.put("Clouds", "Облачно");
    }

}
