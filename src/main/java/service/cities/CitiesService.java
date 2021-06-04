package service.cities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.CityModel;
import org.hibernate.exception.ConstraintViolationException;
import service.hibernateService.CitiesHibernateService;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitiesService {
    static CityModel cityModel = new CityModel();
    public static void setCities(){
        List<String> list = new ArrayList<>();
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .get("https://api.rasp.yandex.net/v3.0/stations_list/?apikey=1324d008-778c-4fca-a057-2c7ce97c7b92&lang=ru_RU&format=json");
        JsonParser jsonParser = new JsonParser();
        JsonArray s = jsonParser.parse(new InputStreamReader(response.asInputStream()))
                .getAsJsonObject().get("countries")
                .getAsJsonArray();
        JsonArray regions;
        JsonArray settlements;
        JsonObject city;
        CityModel cityModel = new CityModel();
        for (int i = 0; i < s.size(); i++) {
            regions = s.get(i).getAsJsonObject().getAsJsonArray("regions");
            String countryName = s.get(i).getAsJsonObject().get("title").toString().replace("\"", "");
            for (int j = 0; j < regions.size(); j++) {
                settlements = regions.get(j).getAsJsonObject().getAsJsonArray("settlements");
                for (int k = 0; k < settlements.size(); k++) {
                    cityModel.clearFields();
                    city = settlements.get(k).getAsJsonObject();
                    String cityName = city.get("title").toString().replace("\"", "");
                    String cityCode;
                    try {
                        cityCode = city.getAsJsonObject("codes").get("yandex_code").toString().replace("\"", "");
                    } catch (NullPointerException e) {
                        cityCode = null;
                    }
                    cityModel.setCityName(cityName.toLowerCase(Locale.ROOT));
                    cityModel.setCountry(countryName);
                    cityModel.setCityCode(cityCode);
                    if (cityModel.getCityCode()!=null) {
                        try {
                            CitiesHibernateService.addCity(cityModel);
                        } catch (ConstraintViolationException e){
                            list.add(cityModel.getCityName());
                        }
                        System.out.println("Добавлен " + cityModel.getCityName() + " " + cityModel.getCountry());
                    }
                }
            }
        }
    }

    public static List getAllCities(){
        return CitiesHibernateService.getAllCities(cityModel);
    }
}
