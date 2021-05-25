import telegram.BotTelegram;
import model.CityModel;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.cities.CitiesService;

import java.util.List;

public class tests {

    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws Exception {
        startBot();
//        test();
    }

    private static void startBot() throws Exception {
//        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/log4j.properties");
        PropertyConfigurator.configure(tests.class.getClassLoader().getResource("log4j.properties"));
        ApiContextInitializer.init();
        botTelegram = new BotTelegram("@KomarTester_bot", "1849882964:AAFau5iv6y880gJ7a8lJ2r8uJ92o6EKf6Hw");
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

        List listCities = CitiesService.getAllCities();
        listCities.forEach((Object listCity) -> {
            CityModel city = (CityModel) listCity;
            if (city.getCityName().equals("Кsdbc cb vрома")) {
                System.out.println(city.getCityCode());
            }
        });
        System.out.println("Данного города нет");
    }
}
