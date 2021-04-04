package service.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class BotCalendarDateConverter {
    public static Date fromStringToDate(String from) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        Date date = sdf.parse(from);
//        date.getTime();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate ld = LocalDate.parse(from,dtf);
        return null;
    }
}
