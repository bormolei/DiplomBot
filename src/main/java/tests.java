import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.cities.CitiesService;
import service.hibernateService.CitiesHibernateService;
import service.telegram.TelegramMethods;
import telegram.BotTelegram;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
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
        if(CitiesHibernateService.haventCities()){
            CitiesService.setCities();
        }
        botTelegram = new BotTelegram("@KomarTester_bot", TelegramMethods.decrypt("39y9cSzNUtGb7JY1ijYD4I3PWvsIV4o3sZxxGRVm9f5eIbG5XrdE7FRgwJGNJpWK"));
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            session = botsApi.registerBot(botTelegram);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        System.out.println("run");
    }

    //2021-03-27 11:56:13.445000
    private static void test() throws Exception {
        Calendar calendar = Calendar.getInstance();
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь" };
        String month = monthNames[1];
        System.out.println(month);
    }
}
