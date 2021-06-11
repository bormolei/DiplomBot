package service.weather;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.telegram.TelegramKeyboard;

import java.util.List;

public class WeatherMethods extends TelegramKeyboard {
    protected static ReplyKeyboard createHoursKeyBoard(List<List<InlineKeyboardButton>> rowList, int day) {
        for (int i = 1; i <= 5; i += 2) {
            String dayStr = "";
            if (i == 1) {
                dayStr = " день";
            } else if (i == 3) {
                dayStr = " дня";
            } else if (i == 5) {
                dayStr = " дней";
            }
            if (i != day) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Погода на " + i + dayStr)
                        .setCallbackData(String.format("day'%s", i)));
            }
        }
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Главное меню")
                .setCallbackData("MainMenu"));
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
