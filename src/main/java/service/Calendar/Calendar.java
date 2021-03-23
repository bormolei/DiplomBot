package service.Calendar;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.Exceptions.MonthException;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class Calendar {
    static List<InlineKeyboardButton> keyboardButtonsMonthYear = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
    static List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
    static List<InlineKeyboardButton> changeButtonsRow = new ArrayList<>();
    static InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    static InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();


    public static ReplyKeyboard createYear(int year) {
        clearKeyBoard();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        createYearKeyBoard(year, rowList);

        return inlineKeyboardMarkup;
    }

    public static ReplyKeyboard createMonth(int month, int year) throws MonthException {
        int localYear = year;
        clearKeyBoard();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
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
                createDaysKeyBoard(31, rowList);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                createDaysKeyBoard(30, rowList);
                break;
            case 2:
                february(rowList);
                break;
            default:
                throw new MonthException("Данного(" + month + ") месяца не существует ", month);

        }

        return inlineKeyboardMarkup;
    }

    private static void february(List<List<InlineKeyboardButton>> rowList) {
        if (!Year.isLeap(LocalDate.now().getYear())) {
            createDaysKeyBoard(28, rowList);
        } else {
            createDaysKeyBoard(29, rowList);
        }
    }

    /**
     * Создание клавиатуры для года
     *
     * @param year
     */
    private static void createYearKeyBoard(int year, List<List<InlineKeyboardButton>> rowList) {
        keyboardButtonsMonthYear.add(new InlineKeyboardButton().setText(Year.of(year).toString())
                .setCallbackData("ChooseYearButton"));
        for (int i = 1; i <= 12; i++) {
            if (i <= 3) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue()) + "'" + year));
            }
            if (i > 3 && i <= 6) {
                keyboardButtonsRow2.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue())+ "'" + year));
            }
            if (i > 6 && i <= 9) {
                keyboardButtonsRow3.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue())+ "'" + year));
            }
            if (i > 9) {
                keyboardButtonsRow4.add(new InlineKeyboardButton().setText(Month.of(i).toString())
                        .setCallbackData("Month''" + (Month.of(i).getValue())+ "'" + year));
            }
        }
//        if (month == 1) {
//            changeButtonsRow.add(new InlineKeyboardButton().setText("<")
//                    .setCallbackData("Month'" + (12)));
//            changeButtonsRow.add(new InlineKeyboardButton().setText(">")
//                    .setCallbackData("Month'" + (month + 1)));
//        } else if (month == 12) {
//            changeButtonsRow.add(new InlineKeyboardButton().setText("<")
//                    .setCallbackData("Month'" + (month - 1)));
//            changeButtonsRow.add(new InlineKeyboardButton().setText(">")
//                    .setCallbackData("Month'" + (1)));
//        } else {
//            changeButtonsRow.add(new InlineKeyboardButton().setText("<")
//                    .setCallbackData("Month'" + (month - 1)));
//            changeButtonsRow.add(new InlineKeyboardButton().setText(">")
//                    .setCallbackData("Month'" + (month + 1)));
//        }
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        inlineKeyboardMarkup.setKeyboard(rowList);
    }

    private static void createDaysKeyBoard(int maxDateOfMonth, List<List<InlineKeyboardButton>> rowList) {
        for (int i = 0; i < maxDateOfMonth; i++) {
            if (i <= 6) {
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s", i + 1)));
            } else if (i > 6 && i <= 13) {
                keyboardButtonsRow2.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s", i + 1)));
            } else if (i > 13 && i <= 20) {
                keyboardButtonsRow3.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s", i + 1)));
            } else if (i > 20 && i <= 27) {
                keyboardButtonsRow4.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s", i + 1)));
            } else if (i > 27 && i <= 30) {
                keyboardButtonsRow5.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                        .setCallbackData(String.format("date'%s", i + 1)));
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

    private static void clearKeyBoard() {
        keyboardButtonsMonthYear.clear();
        keyboardButtonsRow1.clear();
        keyboardButtonsRow2.clear();
        keyboardButtonsRow3.clear();
        keyboardButtonsRow4.clear();
        keyboardButtonsRow5.clear();
        changeButtonsRow.clear();
    }
}
