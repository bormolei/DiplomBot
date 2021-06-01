package telegram;

import Exceptions.Calendar.MonthException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.telegram.TelegramMethods;

public class BotTelegram extends TelegramLongPollingBot {

    String botName;
    String botToken;

    public BotTelegram(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && (message.hasText() || message.hasLocation() || message.hasDocument() || message.hasPhoto())) {
            try {
                TelegramMethods.sendMsg(message, this);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            try {
                TelegramMethods.sendMsgFromCallBack(update.getCallbackQuery(), this);
            } catch (TelegramApiException | MonthException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}