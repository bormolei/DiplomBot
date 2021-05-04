package service.Telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TelegramKeyboard {
    protected static List<InlineKeyboardButton> keyboardButtonsMonthYear = new ArrayList<>();
    protected static List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
    protected static List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
    protected static List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
    protected static List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
    protected static List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
    protected static List<InlineKeyboardButton> changeButtonsRow = new ArrayList<>();
    protected static InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    protected static InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

    public static void clearKeyBoard() {
        keyboardButtonsMonthYear.clear();
        keyboardButtonsRow1.clear();
        keyboardButtonsRow2.clear();
        keyboardButtonsRow3.clear();
        keyboardButtonsRow4.clear();
        keyboardButtonsRow5.clear();
        changeButtonsRow.clear();
    }
}
