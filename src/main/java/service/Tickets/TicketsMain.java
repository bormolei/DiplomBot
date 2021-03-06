package service.Tickets;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.TicketsModel;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.Telegram.TelegramKeyboard;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    static DateTimeFormatter formatter;
    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    public static String getTicketInfo(String findCity) {
        JsonArray regions;
        JsonArray settlements;
        JsonObject city;
        for (int i = 0; i < s.size(); i++) {
            regions = s.get(i).getAsJsonObject().getAsJsonArray("regions");
            for (int j = 0; j < regions.size(); j++) {
                settlements = regions.get(j).getAsJsonObject().getAsJsonArray("settlements");
                for (int k = 0; k < settlements.size(); k++) {
                    city = settlements.get(k).getAsJsonObject();
                    String cityName = city.get("title").toString().replace("\"", "");
                    if (cityName.equals(findCity)) {
                        return city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"", "");
                    }
                    //????????????????
                }

            }


        }
        return "???????????? ?????????? ???? ????????????";
    }

    public static List<Object> getWay(Message message) {
        TelegramKeyboard.clearKeyBoard();
        rowList.clear();
        if (!l.isEmpty()) {
            l.clear();
        }
        formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
        l = Lists.newArrayList(message.getText().split(" "));
        String from = TicketsMain.getTicketInfo(l.get(0));
        String to = TicketsMain.getTicketInfo(l.get(1));
        LocalDate travelDate = LocalDate.parse(l.get(2), formatter);
        List<Object> trainWays = new ArrayList<>();
        String wayName = "??????????????????????: " + l.get(0)
                + "\n????????????????: " + l.get(1)
                + "\n????????: " + l.get(2)
                + "\n???????????????? ??????????????????????";
        trainWays.add(wayName);
        trainWays.add(getRzdURI(from, to, travelDate.toString(), 0));
        return trainWays;
    }

    public static InlineKeyboardMarkup getRzdURI(String from, String to, String date, int recordNumber) {
        TelegramKeyboard.clearKeyBoard();
        rowList.clear();
        String rasp = "https://api.rasp.yandex.net/v3.0/search/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&format=json&from=" + from + "&to=" + to + "&lang=ru_RU&date=" + date + "&transport_types=train";
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(rasp);
        JsonObject jo = jsonParser.parse(new InputStreamReader(response.asInputStream()))
                .getAsJsonObject();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        int nextPage = recordNumber + 5;
        int previousPage;
        if (recordNumber > 4) {
            previousPage = recordNumber - 5;
        } else {
            previousPage = 0;
        }
        JsonArray segments = jo.get("segments").getAsJsonArray();
        if (segments.size() < 6) {
            ticketsKeyboard(from, to, date, segments, recordNumber, segments.size());
        } else {
            ticketsKeyboard(from, to, date, segments, recordNumber, recordNumber + 5);
        }


        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);
        rowList.add(changeButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private static void ticketsKeyboard(String from, String to, String date, JsonArray segments, int startPoint, int endPoint) {

        for (int i = 0; i < endPoint; startPoint++, i++) {
            LocalDateTime ldDeparture = LocalDateTime.parse(segments.get(startPoint).getAsJsonObject()
                    .get("departure").toString().replace("\"", ""), formatter);
            String departureTime = ldDeparture.toLocalTime().toString();
            LocalDateTime ldArrival = LocalDateTime.parse(segments.get(startPoint).getAsJsonObject()
                    .get("arrival").toString().replace("\"", ""), formatter);
            String arrivalTime = ldArrival.toLocalTime().toString();
            String number = segments
                    .get(startPoint).getAsJsonObject()
                    .get("thread").getAsJsonObject()
                    .get("number").toString().replace("\"", "");

            String wayTime = "??????????????????????: " + departureTime + " - ????????????????: " + arrivalTime;
            if (i == 0) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(wayTime)
                        .setUrl("https://travel.yandex.ru/trains/order/?adults=1&fromId=" + from + "&&number=" + number + "&time=" + departureTime + "&toId=" + to + "&&when=" + date));
            } else if (i == 1) {
                keyboardButtonsRow2.add(new InlineKeyboardButton().setText(wayTime)
                        .setUrl("https://travel.yandex.ru/trains/order/?adults=1&fromId=" + from + "&&number=" + number + "&time=" + departureTime + "&toId=" + to + "&&when=" + date));
            } else if (i == 2) {
                keyboardButtonsRow3.add(new InlineKeyboardButton().setText(wayTime)
                        .setUrl("https://travel.yandex.ru/trains/order/?adults=1&fromId=" + from + "&&number=" + number + "&time=" + departureTime + "&toId=" + to + "&&when=" + date));
            } else if (i == 3) {
                keyboardButtonsRow4.add(new InlineKeyboardButton().setText(wayTime)
                        .setUrl("https://travel.yandex.ru/trains/order/?adults=1&fromId=" + from + "&&number=" + number + "&time=" + departureTime + "&toId=" + to + "&&when=" + date));
            } else if (i == 4) {
                keyboardButtonsRow5.add(new InlineKeyboardButton().setText(wayTime)
                        .setUrl("https://travel.yandex.ru/trains/order/?adults=1&fromId=" + from + "&&number=" + number + "&time=" + departureTime + "&toId=" + to + "&&when=" + date));
            }
        }
    }

    public static InlineKeyboardMarkup getAccept() {
        TelegramKeyboard.clearKeyBoard();
        rowList.clear();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("?????? ??????????")
                .setCallbackData("Ticket'get"));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("???? ??????????")
                .setCallbackData("Ticket'cancel"));
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static String checkData(TicketsModel ticketsModel,String data){
        formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
        if(ticketsModel.getDepartureCity()==null||ticketsModel.getArrivalCity()==null){
            if(!s.toString().contains(data)) {
                return "?????????????? ???????????? ?????? ?? ???????? ???????????? " + data;
            }
        } else if (ticketsModel.getDepartureDate()==null ){
            try{
                LocalDate.parse(data,formatter);
            }catch (DateTimeParseException exception){
                return "?????????????? ?????????????? ???????? ??????????????????????";
            }
        }
        return "OK";
    }
}
