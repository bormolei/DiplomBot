package service.Tickets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TicketsMain {
    static String getAllStationsURI = "https://api.rasp.yandex.net/v3.0/stations_list/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&lang=ru_RU&format=json";
    static JsonParser jsonParser = new JsonParser();
    static Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .get(getAllStationsURI);
    static JsonArray s = jsonParser.parse(new InputStreamReader(response.asInputStream()))
            .getAsJsonObject().get("countries")
            .getAsJsonArray();

    public static String getTicketInfo(String findCity){
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
            country = s.get(i).getAsJsonObject().get("title").toString().replace("\"","");
            regions = s.get(i).getAsJsonObject().getAsJsonArray("regions");
            for (int j = 0; j < regions.size(); j++) {
                region = regions.get(j).getAsJsonObject().get("title").toString().replace("\"", "");
                settlements = regions.get(j).getAsJsonObject().getAsJsonArray("settlements");
                for (int k = 0; k < settlements.size(); k++) {
                    city = settlements.get(k).getAsJsonObject();
                    String cityName = city.get("title").toString().replace("\"", "");
                    if (cityName.equals(findCity)) {
                        return city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"","");
//                        return country + " " + region + " " + cityName + " " + city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"","");
                    }
                    //ДОДЕЛАТЬ
                }

            }


        }
        return "Данный город не найден";
    }

    public static void getWay(String from, String to, String date){
        String rasp = "https://api.rasp.yandex.net/v3.0/search/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&format=json&from="+from+"&to="+to+"&lang=ru_RU&date=2021-04-30";
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get(rasp);
        JsonObject jo = jsonParser.parse(new InputStreamReader(response.asInputStream()))
                .getAsJsonObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        LocalDateTime ld= LocalDateTime.parse(jo.get("segments").getAsJsonArray().get(2).getAsJsonObject()
                .get("departure").toString().replace("\"",""),formatter);
        String time = ld.getHour()+"."+ld.getMinute();
//        https://travel.yandex.ru/trains/order/?adults=1&fromId=c7&number=715Я&time=07.12&toId=c213&toName=Москва&when=2021-04-30
        System.out.println("https://travel.yandex.ru/trains/order/?adults=1&fromId="+from+"&&number=715Я&time="+time+"&toId="+to+"&&when=2021-04-30");
    }
}
