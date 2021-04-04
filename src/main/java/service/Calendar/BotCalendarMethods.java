package service.Calendar;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import Exceptions.Calendar.MonthException;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class BotCalendarMethods {
    static List<InlineKeyboardButton> keyboardButtonsMonthYear = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
    static List<InlineKeyboardButton> changeButtonsRow = new ArrayList<>();
    static InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    static InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

    protected static void clearKeyBoard() {
        keyboardButtonsMonthYear.clear();
        keyboardButtonsRow1.clear();
        keyboardButtonsRow2.clear();
        keyboardButtonsRow3.clear();
        keyboardButtonsRow4.clear();
        keyboardButtonsRow5.clear();
        changeButtonsRow.clear();
    }

    /**
     * Создание клавиатуры для года
     *
     * @param year
     * @return
     */
    protected static ReplyKeyboard createYearKeyBoard(int year, List<List<InlineKeyboardButton>> rowList) {
        keyboardButtonsMonthYear.add(new InlineKeyboardButton().setText(Year.of(year).toString())
                .setCallbackData("ChooseYearButton"));
        for (int i = 1; i <= 12; i++) {
            if (i <= 3) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue()) + "'" + year));
            }
            if (i > 3 && i <= 6) {
                keyboardButtonsRow2.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue()) + "'" + year));
            }
            if (i > 6 && i <= 9) {
                keyboardButtonsRow3.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue()) + "'" + year));
            }
            if (i > 9) {
                keyboardButtonsRow4.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue()) + "'" + year));
            }
        }
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    protected static void createDaysKeyBoard(int maxDateOfMonth, int month, int year, List<List<InlineKeyboardButton>> rowList) {
        for (int i = 0; i < maxDateOfMonth; i++) {
            if (i <= 6) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s'%s'%s", i + 1, month, year)));
            } else if (i <= 13) {
                keyboardButtonsRow2.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s'%s'%s", i + 1, month, year)));
            } else if (i <= 20) {
                keyboardButtonsRow3.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s'%s'%s", i + 1, month, year)));
            } else if (i <= 27) {
                keyboardButtonsRow4.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s'%s'%s", i + 1, month, year)));
            } else if (i <= 30) {
                keyboardButtonsRow5.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s'%s'%s", i + 1, month, year)));
            }
        }
        rowList.add(keyboardButtonsMonthYear);
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);
        rowList.add(changeButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);
    }

    static ReplyKeyboard createMonthKeyBoard(int month, int year, List<List<InlineKeyboardButton>> rowList) throws MonthException {
        int localYear = year;
        if (month == 1) {
            changeButtonsRow.add(new InlineKeyboardButton().setText("<")
                    .setCallbackData("Month''" + (12) + "'" + --localYear));
            changeButtonsRow.add(new InlineKeyboardButton().setText(">")
                    .setCallbackData("Month''" + (month + 1) + "'" + year));
        } else if (month == 12) {
            changeButtonsRow.add(new InlineKeyboardButton().setText("<")
                    .setCallbackData("Month''" + (month - 1) + "'" + year));
            changeButtonsRow.add(new InlineKeyboardButton().setText(">")
                    .setCallbackData("Month''" + (1) + "'" + ++localYear));
        } else {
            changeButtonsRow.add(new InlineKeyboardButton().setText("<")
                    .setCallbackData("Month''" + (month - 1) + "'" + localYear));
            changeButtonsRow.add(new InlineKeyboardButton().setText(">")
                    .setCallbackData("Month''" + (month + 1) + "'" + localYear));
        }
        keyboardButtonsMonthYear.add(new InlineKeyboardButton().setText(Month.of(month).toString() + " " + year)
                .setCallbackData("ChooseMonthButton'''" + localYear));
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                BotCalendarMethods.createDaysKeyBoard(31, month, year, rowList);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                BotCalendarMethods.createDaysKeyBoard(30, month, year, rowList);
                break;
            case 2:
                february(month, year, rowList);
                break;
            default:
                throw new MonthException("Данного(" + month + ") месяца не существует ", month);

        }
        return inlineKeyboardMarkup;
    }

    private static void february(int month, int year, List<List<InlineKeyboardButton>> rowList) {
        if (!Year.isLeap(LocalDate.now().getYear())) {
            BotCalendarMethods.createDaysKeyBoard(28, month, year, rowList);
        } else {
            BotCalendarMethods.createDaysKeyBoard(29, month, year, rowList);
        }
    }

    protected static ReplyKeyboard createKeyBoardForDay(int date, int month, int year, List<List<InlineKeyboardButton>> rowList) {
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Добавить заметку")
                .setCallbackData(String.format("add'%s-%s-%s", date, month, year)));
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Назад")
                .setCallbackData("Month''" + month + "'" + year));
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }



}
