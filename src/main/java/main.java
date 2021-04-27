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
        String str = "https://api.rasp.yandex.net/v3.0/stations_list/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&lang=ru_RU&format=json";
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(str);
        JsonArray s = new JsonParser()
                .parse(new InputStreamReader(response.asInputStream()))
                .getAsJsonObject().get("countries")
                .getAsJsonArray();
//                .get(200)//При необходимости сделать цикл
//                .getAsJsonObject();
//                .get("text")
//                .toString()
//                .replace("\"","");
        String myCity = "Москва";
        s.get(0);
        Map<Integer, String> yandexRasp = new HashMap<>();
//        for (int i = 0; i < s.size(); i++) {
//            String country = s.get(i).getAsJsonObject().get("title").toString();
//            yandexRasp.put(i, country);
//        }
//        yandexRasp.entrySet().forEach(System.out::println);
        String country;
        String region;
        Map<String, String> l = new HashMap();
        List tset = new ArrayList();
        JsonArray regions;
        JsonArray settlements;
        JsonObject city;
        for (int i = 0; i < s.size(); i++) {
            country = s.get(i).getAsJsonObject().get("title").toString().replace("\"","");
            regions = s.get(i).getAsJsonObject().getAsJsonArray("regions");
            for (int j = 0; j < regions.size(); j++) {
                region = regions.get(j).getAsJsonObject().get("title").toString().replace("\"", "");
                settlements = regions.get(j).getAsJsonObject().getAsJsonArray("settlements");
                for (int k = 0; k < settlements.size(); k++) {
                    city = settlements.get(k).getAsJsonObject();
                    String cityName = city.get("title").toString().replace("\"", "");
                    if (cityName.equals(myCity)) {
                        System.out.println(country + " " + region + " " + cityName + " " + city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"",""));
                    }
                    ; //ДОДЕЛАТЬ
                }

            }


        }
        System.out.println("finish");
//        JsonArray json = new JsonArray(response);
//        Long l = 1123699229L;
//        List<? extends MainModel> list = BotCalendarService.getAllUserTasksForDay(l);
//        BotCalendarModel bcm = (BotCalendarModel) list.get(0);
//        System.out.println(bcm.getAddUpdFlag());
    }
}
