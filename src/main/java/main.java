import Telegram.BotTelegram;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.Tickets.TicketsMain;

import java.text.ParseException;

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
        String from = TicketsMain.getTicketInfo("Кострома");
        String to = TicketsMain.getTicketInfo("Москва");
        TicketsMain.getRzdURI(from, to,"");
        System.out.println("finish");
//        JsonArray json = new JsonArray(response);
//        Long l = 1123699229L;
//        List<? extends MainModel> list = BotCalendarService.getAllUserTasksForDay(l);
//        BotCalendarModel bcm = (BotCalendarModel) list.get(0);
//        System.out.println(bcm.getAddUpdFlag());
    }
}
