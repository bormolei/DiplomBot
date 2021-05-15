import Telegram.BotTelegram;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.BotCalendarModel;
import model.MainModel;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.HibernateService.BotCalendarService;
import service.Tickets.TicketsMain;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;

public class main {
    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws ParseException {
        test();
//        startBot();
    }

    private static void startBot() {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/log4j.properties");
        ApiContextInitializer.init();
        botTelegram = new BotTelegram("PA_Komar_bot", "1610562314:AAGmMW_XP74L2Ow8wJM_wIREVC8w3WcrqiQ");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            session = botsApi.registerBot(botTelegram);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        System.out.println("run");
    }

    //2021-03-27 11:56:13.445000
    private static void test() throws ParseException {
        String from = TicketsMain.getTicketInfo("Кострома");
        String to = TicketsMain.getTicketInfo("Москва");
        TicketsMain.getWay(from, to,"");
        System.out.println("finish");
//        JsonArray json = new JsonArray(response);
//        Long l = 1123699229L;
//        List<? extends MainModel> list = BotCalendarService.getAllUserTasksForDay(l);
//        BotCalendarModel bcm = (BotCalendarModel) list.get(0);
//        System.out.println(bcm.getAddUpdFlag());
    }
}
