import Telegram.BotTelegram;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.TicketsModel;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.HibernateService.TicketsService;
import service.Tickets.TicketsMain;
import service.Tickets.TicketsMethods;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tests {

    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws ParseException {
//        startBot();
        test();
    }

    private static void startBot() {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/log4j.properties");
        ApiContextInitializer.init();
        TicketsMain.getStations();
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
    private static void test() {
        String getAllStationsURI = "https://api.rasp.yandex.net/v3.0/stations_list/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&lang=ru_RU&format=json";

        String response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(getAllStationsURI).asString();
        Matcher matcher = Pattern
                .compile("(?s)(?:(?:.*?\\\"yandex_code\\\"):\\s\\\"(.*?)\\\".*?(?:\\\"title\\\": \\\"Кострома\\\".*?\\\"title\\\"))?.*")
                .matcher(response);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        } else{
            System.out.print("ERROR");
        }
    }
}
