package service.exchangeRates;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.JSONArray;
import org.json.XML;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.telegram.TelegramKeyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRates extends TelegramKeyboard {
    static Map<String, Double> curs = setCurrencies();
    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    public static Map<String, Double> setCurrencies() {
        Map<String, Double> cursTmp = new HashMap<>();
        String response = RestAssured.given()
                .contentType(ContentType.XML)
                .get("http://www.cbr.ru/scripts/XML_daily.asp")
                .getBody().asString();

        JSONArray jo = XML.toJSONObject(String.valueOf(response)).getJSONObject("ValCurs").getJSONArray("Valute");
        for (int i = 0; i < jo.length(); i++) {
            String currency = jo.getJSONObject(i).get("Name").toString();
            if (currency.contains("США") || currency.contains("Евро") || currency.contains("Фунт")) {
                currency += "-" + jo.getJSONObject(i).get("CharCode").toString();
                Double value = Double.parseDouble(jo.getJSONObject(i).get("Value").toString().replace(",", "."));
                cursTmp.put(currency, value);
            }
        }
        if(cursTmp.size() == 3){
            return cursTmp;
        } else {
            return null;
        }
    }

    public static ReplyKeyboard setCurries(Double amount) {
        rowList.clear();
        TelegramKeyboard.clearKeyBoard();
        for (Map.Entry<String, Double> currries : curs.entrySet()) {
            String key = currries.getKey();
            keyboardButtonsRow1.add(new InlineKeyboardButton().setText(key.split("-")[0])
                    .setCallbackData("Rates'" + key.split("-")[1] + "'" + amount));
        }
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static String convert(CallbackQuery callbackQuery) {
        String currency = callbackQuery.getData().split("'")[1];
        Double amount = Double.parseDouble(callbackQuery.getData().split("'")[2]);
        for (Map.Entry<String, Double> currries : curs.entrySet()) {
            String key = currries.getKey();
            Double value = currries.getValue();
            if (key.contains(currency)) {
                return "Курс валюты: 1 " + key.split("-")[0] + " равен " + String.format("%.2f", value) + " Рублей"
                        + "\n\nРубли: " + amount + "\n" + key.split("-")[0] + ": " + String.format("%.2f", amount / value);
            }
        }
        return null;
    }
}
