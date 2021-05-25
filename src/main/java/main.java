import telegram.BotTelegram;
import org.apache.log4j.PropertyConfigurator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotSession;

public class main {
    static BotSession session = null;
    static BotTelegram botTelegram = null;

    public static void main(String[] args) throws Exception {
        startBot();
    }

    private static void startBot() throws Exception {
//        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/log4j.properties");
        PropertyConfigurator.configure(main.class.getClassLoader().getResource("log4j.properties"));
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

}
