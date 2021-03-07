
import model.Telegram.BotTelegram;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Set;

public class main {
    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) {
//        test();
        startBot();
    }

    private static void startBot() {
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

    private static void test() {
        System.out.println(LocalDate.now().getMonth().getValue());
    }
}
