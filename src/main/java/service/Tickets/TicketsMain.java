package service.Tickets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.Telegram.TelegramKeyboard;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TicketsMain extends TelegramKeyboard {
    static String getAllStationsURI = "https://api.rasp.yandex.net/v3.0/stations_list/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&lang=ru_RU&format=json";
    static JsonParser jsonParser = new JsonParser();
    static Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .get(getAllStationsURI);
    static JsonArray s = jsonParser.parse(new InputStreamReader(response.asInputStream()))
            .getAsJsonObject().get("countries")
            .getAsJsonArray();
    static List<String> l = new ArrayList<>();
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    public static String getTicketInfo(String findCity) {
        s.get(0);
        Map<Integer, String> yandexRasp = new HashMap<>();
//        for (int i = 0; i < s.size(); i++) {
//            String country = s.get(i).getAsJsonObject().get("title").toString();
//            yandexRasp.put(i, country);
//        }
//        yandexRasp.entrySet().forEach(System.out::println);
        String country;
        String region;
        JsonArray regions;
        JsonArray settlements;
        JsonObject city;
        for (int i = 0; i < s.size(); i++) {
            country = s.get(i).getAsJsonObject().get("title").toString().replace("\"", "");
            regions = s.get(i).getAsJsonObject().getAsJsonArray("regions");
            for (int j = 0; j < regions.size(); j++) {
                region = regions.get(j).getAsJsonObject().get("title").toString().replace("\"", "");
                settlements = regions.get(j).getAsJsonObject().getAsJsonArray("settlements");
                for (int k = 0; k < settlements.size(); k++) {
                    city = settlements.get(k).getAsJsonObject();
                    String cityName = city.get("title").toString().replace("\"", "");
                    if (cityName.equals(findCity)) {
                        return city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"", "");
//                        return country + " " + region + " " + cityName + " " + city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"","");
                    }
                    //ДОДЕЛАТЬ
                }

            }


        }
        return "Данный город не найден";
    }

    public static void getWay(Message message, SendMessage sendMessage) {
        if (!l.isEmpty()) {
            l.clear();
        }
        l = Arrays.asList(message.getText().split(" "));
        String from = TicketsMain.getTicketInfo(l.get(0));
        String to = TicketsMain.getTicketInfo(l.get(1));
        LocalDate travelDate = LocalDate.parse(l.get(2), formatter);
        sendMessage.setText("test").setReplyMarkup(getRzdURI(from, to, travelDate.toString()));
    }

    public static ReplyKeyboard getRzdURI(String from, String to, String date) {
        String rasp = "https://api.rasp.yandex.net/v3.0/search/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&format=json&from=" + from + "&to=" + to + "&lang=ru_RU&date=" + date;
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(rasp);

        JsonObject jo = jsonParser.parse(new InputStreamReader(response.asInputStream()))
                .getAsJsonObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        LocalDateTime ld = LocalDateTime.parse(jo.get("segments").getAsJsonArray().get(2).getAsJsonObject()
                .get("departure").toString().replace("\"", ""), formatter);
        String time = ld.getHour() + "." + ld.getMinute();
        String number = jo.get("segments").getAsJsonArray()
                .get(2).getAsJsonObject()
                .get("thread").getAsJsonObject()
                .get("number").toString().replace("\"", "");
        JsonArray segments = jo.get("segments").getAsJsonArray();

//        for (int i = 0; i < segments.size(); i++) {
//            if (segments.size() > 6) {
//                //добавить прокручивание страниц в нижнюю строку
//            }
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Test")
                .setUrl("https://travel.yandex.ru/trains/order/?adults=1&fromId=" + from + "&&number=" + number + "&time=" + time + "&toId=" + to + "&&when=" + date));
//        }
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
//        https://travel.yandex.ru/trains/order/?adults=1&fromId=c7&number=715Я&time=07.12&toId=c213&toName=Москва&when=2021-04-30
    }
}
