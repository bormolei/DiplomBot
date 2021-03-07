package service.Calendar;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.Exceptions.MonthException;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class Calendar {
    static List<InlineKeyboardButton> keyboardButtonsRow1week = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow2week = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow3week = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow4week = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow5week = new ArrayList<>();
    static List<InlineKeyboardButton> changeButtonsRow = new ArrayList<>();
    static InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    static InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();


    public static ReplyKeyboard createMonth(int month) throws MonthException {
        clearKeyBoard();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                createKeyBoard(31, rowList);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                createKeyBoard(30, rowList);
                break;
            case 2:
                february(rowList);
                break;
            default:
                throw new MonthException("Данного(" + month + ") месяца не существует ", month);

        }
        changeButtonsRow.add(new InlineKeyboardButton().setText("<")
                .setCallbackData("Previous'" + month));
        changeButtonsRow.add(new InlineKeyboardButton().setText(">")
                .setCallbackData("Next'" + month));
        return inlineKeyboardMarkup;
    }

    private static void february(List<List<InlineKeyboardButton>> rowList) {
        if (!Year.isLeap(LocalDate.now().getYear())) {
            createKeyBoard(28, rowList);
        } else {
            createKeyBoard(29, rowList);
        }
    }

    private static void createKeyBoard(int maxDateOfMonth, List<List<InlineKeyboardButton>> rowList) {
        for (int i = 0; i < maxDateOfMonth; i++) {
            if (i <= 6) {
                keyboardButtonsRow1week.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date %s", i + 1)));
            } else if (i > 6 && i <= 13) {
                keyboardButtonsRow2week.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date %s", i + 1)));
            } else if (i > 13 && i <= 20) {
                keyboardButtonsRow3week.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date %s", i + 1)));
            } else if (i > 20 && i <= 27) {
                keyboardButtonsRow4week.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date %s", i + 1)));
            } else if (i > 27 && i <= 30) {
                keyboardButtonsRow5week.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date %s", i + 1)));
            }
        }
        rowList.add(keyboardButtonsRow1week);
        rowList.add(keyboardButtonsRow2week);
        rowList.add(keyboardButtonsRow3week);
        rowList.add(keyboardButtonsRow4week);
        rowList.add(keyboardButtonsRow5week);
        rowList.add(changeButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
    }

    private static void clearKeyBoard() {
        keyboardButtonsRow1week.clear();
        keyboardButtonsRow2week.clear();
        keyboardButtonsRow3week.clear();
        keyboardButtonsRow4week.clear();
        keyboardButtonsRow5week.clear();
        changeButtonsRow.clear();
    }
}
