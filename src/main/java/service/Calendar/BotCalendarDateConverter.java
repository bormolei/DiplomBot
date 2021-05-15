package service.Calendar;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BotCalendarDateConverter {
    public static LocalDate fromStringToDate(String from) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

        return LocalDate.parse(from, formatter);
    }

    public static LocalTime parceTime(String time){
        return LocalTime.parse(time);
    }
}
