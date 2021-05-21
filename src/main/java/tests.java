import Telegram.BotTelegram;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.Tickets.TicketsMain;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class tests {

    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws Exception {
        startBot();
//        test();
    }

    private static void startBot() throws Exception {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/log4j.properties");
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
        TicketsMain.getTicketInfo("Кострома");
//        String textResult = "";
//        String text = RestAssured.given()
//                .contentType(ContentType.JSON)
//                .get("https://api.rasp.yandex.net/v3.0/stations_list/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&lang=ru_RU&format=json")
//                .asString();
//
//        Matcher matcher1 = Pattern
//                .compile("\\{\"countries\": \\[(.*)")
//                .matcher(text);
//        Map<Object, Object> map = new HashMap<Object, Object>();
//
//        if (matcher1.find()) {
//            String t = matcher1.group(1);
//            textResult = t.substring(0, t.length() - 2);
//        } else throw new Exception("");
//
//        Stream<String> arr = Arrays.stream(textResult.split(",\\s+(?=\\{\\\"regions\\\":)"));
//
//        try {
//            arr.forEach(str ->
//                    new JsonParser().parse(str)
//                            .getAsJsonObject()
//                            .getAsJsonArray("regions")
//                            .forEach(region ->
//                                    region.getAsJsonObject()
//                                            .getAsJsonArray("settlements")
//                                            .forEach(settle -> {
//                                                Object yandex_code = settle.getAsJsonObject().getAsJsonObject("codes")
//                                                        .get("yandex_code");
//
//                                                Object title = settle.getAsJsonObject()
//                                                        .get("title")
//                                                        .getAsString();
//
//                                                map.put(title, yandex_code);
//                                            })
//                            )
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        map.forEach((k, v) -> System.out.println("code:" + k + " value:" + v));
    }
}
