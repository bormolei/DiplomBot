package service.Calendar;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import Exceptions.Calendar.MonthException;

import java.util.ArrayList;
import java.util.List;

public class Calendar{
    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

    public static ReplyKeyboard createYear(int year) {
        CalendarService.clearKeyBoard();
        rowList.clear();
        return CalendarService.createYearKeyBoard(year, rowList);
    }

    public static ReplyKeyboard createMonth(int month, int year) throws MonthException {
        CalendarService.clearKeyBoard();
        rowList.clear();
        return CalendarService.createMonthKeyBoard(month,year,rowList);
    }


}
