import Telegram.BotTelegram;
import model.TicketsModel;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;
import service.HibernateService.TicketsService;
import service.Tickets.TicketsMethods;

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
    private static void test() {
        TicketsModel ticketsModel = new TicketsModel();
        try {
//            ticketsModel = TicketsService.getTicketInfo(381175043L);
        } catch (IndexOutOfBoundsException e) {
//            ticketsModel.setChatId(381175043L);
            TicketsService.addNewTicket(ticketsModel);
        }
        System.out.println(TicketsMethods.ticketInfo(ticketsModel));
    }
}
