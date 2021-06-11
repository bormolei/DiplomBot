import model.FileStorageModel;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.calendar.BotCalendarMethods;
import service.cities.CitiesService;
import service.exchangeRates.ExchangeRates;
import service.hibernateService.CitiesHibernateService;
import service.hibernateService.FileStorageHibernateService;
import service.telegram.TelegramMethods;
import service.tickets.TicketsMain;
import service.weather.WeatherBot;
import telegram.BotTelegram;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class tests {

    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws Exception {
        startBot();
//        test();
    }

    private static void startBot() throws Exception {
        PropertyConfigurator.configure(tests.class.getClassLoader().getResource("log4j.properties"));
        ApiContextInitializer.init();
        try {
            TicketsMain.getTicketInfo("Москва");
            System.out.println("Сервис города и транспортные билеты работают");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка в работе сервисов города или транспортные билеты");
        }
        System.out.println("Сервис календарь работает");
        if (ExchangeRates.setCurrencies() != null) {
            System.out.println("Сервис курса валют работает");
        }
        System.out.println("Сервис \"Мои файлы\" работает");
        WeatherBot weatherBot = new WeatherBot();
        if (!weatherBot.getReadyForecast("Moscow", 1).equals("Сервис сейчас не работает")) {
            System.out.println("Сервис прогноза погоды работает");
        } else {
            System.out.println("В сервисе прогноза погоды ошибка");
        }
        botTelegram = new BotTelegram("@KomarTester_bot", TelegramMethods.decrypt("39y9cSzNUtGb7JY1ijYD4I3PWvsIV4o3sZxxGRVm9f5eIbG5XrdE7FRgwJGNJpWK"));
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            session = botsApi.registerBot(botTelegram);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        System.out.println("Бот запущен");
    }

    //2021-03-27 11:56:13.445000
    private static void test() throws Exception {
        Integer chatId = 3;
        List f = FileStorageHibernateService.getFile("Метод решающих матриц",chatId);
        System.out.println();
    }
}
