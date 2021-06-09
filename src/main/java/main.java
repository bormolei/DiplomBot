import service.cities.CitiesService;
import service.exchangeRates.ExchangeRates;
import service.hibernateService.CitiesHibernateService;
import service.telegram.TelegramMethods;
import service.tickets.TicketsMain;
import service.weather.WeatherBot;
import telegram.BotTelegram;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

public class main {
    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws Exception {
        startBot();
    }

    private static void startBot() {
        PropertyConfigurator.configure(main.class.getClassLoader().getResource("log4j.properties"));
        ApiContextInitializer.init();
        try {
            if (CitiesHibernateService.haventCities()) {
                CitiesService.setCities();
            }
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
        botTelegram = new BotTelegram("PA_Komar_bot", TelegramMethods.decrypt("8hYCCliaQH5MUBUSvdpGJr+/fMh85pAf9nfeYWYzsd/XYwZYz+WB9BR/RYx4URdH"));
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            session = botsApi.registerBot(botTelegram);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        System.out.println("Бот запущен");
    }

}
