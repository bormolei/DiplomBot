package model.Telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class BotTelegram extends TelegramLongPollingBot {
    static ArrayList<Boolean> flags = new ArrayList<>(); //1 - погода; 2 -тест2; 3-тест3
    {
        flags.add(false);
        flags.add(false);
        flags.add(false);
    }
    static String botName;
    static String botToken;

    public BotTelegram(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            try {
                TelegramMethods.sendMsg(message, this,flags);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if(update.hasCallbackQuery()){
            try {
                execute(new SendMessage().setText(
                        update.getCallbackQuery().getData())
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException e) {
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
