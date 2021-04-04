import Telegram.BotTelegram;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class main {
    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws ParseException {
//        test();
        startBot();
    }

    private static void startBot() {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/log4j.properties");
        ApiContextInitializer.init();
        botTelegram = new BotTelegram("PA_Komar_bot", "1610562314:AAGmMW_XP74L2Ow8wJM_wIREVC8w3WcrqiQ");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            session = botsApi.registerBot(botTelegram);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        System.out.println("run");
    }

    //2021-03-27 11:56:13.445000
    private static void test() throws ParseException {

//        List l = BotCalendarService.getAllTasksForDayForUser(100003L);
//        List l2 = new ArrayList();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        for (int i = 0; i < l.size(); i++) {
//            ((BotCalendar) l.get(0)).getDate();
//            l2.add(new Timestamp(format.parse(((BotCalendar) l.get(0)).getDate().toString()).getTime()));
//        }
//        System.out.println();
        String datestr = "2021-03-27 11:56";
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Timestamp ts = new Timestamp(format.parse(datestr).getTime());
//        Instant instant = ts.toInstant();

//        BotCalendar calendar = new BotCalendar();
//        calendar.setChatId(100003L);
//        calendar.setDate(ts);
//        calendar.setComment("test");
//        HibernateController.doHibernateAction(calendar, Actions.SAVE);
//
        Long mills = (long) (1616934194L * 1000);
        LocalDateTime date =  LocalDateTime.parse("2021-03-27 11:56", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(mills);
        format.format(calendar1.getTime());
        System.out.println();
    }
}
