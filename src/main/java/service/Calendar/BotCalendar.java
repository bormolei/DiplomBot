package service.Calendar;

import model.BotCalendarModel;
import model.MainModel;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import Exceptions.Calendar.MonthException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BotCalendar {
    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    public static ReplyKeyboard createYear(int year) {
        BotCalendarMethods.clearKeyBoard();
        rowList.clear();
        return BotCalendarMethods.createYearKeyBoard(year, rowList);
    }

    public static ReplyKeyboard createMonth(int month, int year) throws MonthException {
        BotCalendarMethods.clearKeyBoard();
        rowList.clear();
        return BotCalendarMethods.createMonthKeyBoard(month, year, rowList);
    }

    //Пасхалку для Вована(чотыре(4.04 4:44))
    public static ReplyKeyboard taskList(int date, int month, int year) {
        BotCalendarMethods.clearKeyBoard();
        rowList.clear();
        return BotCalendarMethods.createKeyBoardForDay(date, month, year, rowList);
    }

    public static Integer hasUserDay(List<? extends MainModel> userDays, LocalDate ld) {
        BotCalendarModel botCalendarModel;
        for (int i = 0; i <userDays.size(); i++) {
            botCalendarModel = (BotCalendarModel) userDays.get(i);
            if (botCalendarModel.getDate().equals(ld)) {
                botCalendarModel.setAddUpdFlag(true);
                return i;
            }
        }

        return -1;
    }

    public static void addTask(String task) {

    }

}
