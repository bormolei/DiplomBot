package service.Weather;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.Telegram.TelegramKeyboard;

import java.util.List;

public class WeatherMethods extends TelegramKeyboard {
    protected static ReplyKeyboard createHoursKeyBoard(List<List<InlineKeyboardButton>> rowList,int day) {
        for (int i = 1; i <= 5; i+=2) {
            if(i!=day) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(String.valueOf(i))
                        .setCallbackData(String.format("day'%s", i)));
            }
        }
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
