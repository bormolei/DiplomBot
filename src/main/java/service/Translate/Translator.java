package service.Translate;

import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Класс переводчик с русского на английский
 */
public class Translator {
    static String Api_Call = "https://translate.api.cloud.yandex.net/translate/v2/translate";

    /**
     * Метод перевода из русского на английский
     * @param text город для поиска
     * @return переведенное значение
     *
     */
    public static String downloadJsonRawData(String text) throws Exception {

        ArrayList<String> words = new ArrayList<>();
        words.add(text);
        JSONObject jo = new JSONObject();
        jo.put("folder_id", "b1gg3ejg3nhblhts8u9l");
        jo.put("texts", words);
        jo.put("targetLanguageCode", "en");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Api-Key AQVN1B5MmzmAtWxL7xHB8xP0vkfJ_4YPjf0ORl8I")
                .body(jo.toString()).post(Api_Call);


        return new JsonParser()
                .parse(new InputStreamReader(response.asInputStream()))
                .getAsJsonObject().get("translations")
                .getAsJsonArray()
                .get(0)//При необходимости сделать цикл
                .getAsJsonObject()
                .get("text")
                .toString()
                .replace("\"","");
    }


}
