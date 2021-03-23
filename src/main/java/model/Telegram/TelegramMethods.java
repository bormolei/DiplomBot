package model.Telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.Bot;
import service.Calendar.Calendar;
import service.Exceptions.MonthException;
import service.WeatherParser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TelegramMethods {

    private static WeatherParser weatherParser = new Bot();
    private static SendMessage sendMessage;
    private static EditMessageText editMessageText;

    private static void messageOptions(Message message) {
        sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
    }

    private static void editMessageOptions(Message message) {
        editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setMessageId(message.getMessageId());
    }

    public static void sendMsg(Message message, BotTelegram botTelegram, ArrayList<Boolean> flags) throws TelegramApiException, MonthException {
        messageOptions(message);

        //Исправить обработку выбора режима
        if (!flags.get(1)) {
            sendMessage.setText("Выберите режим");
        }

        if (flags.get(1)) {
            sendMessage.setText(weatherParser.getReadyForecast(message.getText()));
        }

        try {
            setButtons(sendMessage);
            if (!check(flags)) {
                int currentMonth = LocalDate.now().getMonth().getValue();
                switch (message.getText()) {
                    case "Погода":
                        sendMessage.setText("Введите название города.Например: \"Москва\" или \"Moscow\"");
                        flags.set(1, true);
                        break;
                    case "Календарь":
                        sendMessage.setText("Выберите число")
                                .setReplyMarkup(Calendar.createMonth(currentMonth,LocalDate.now().getYear()));
                        flags.set(2, true);
                        break;
                    case "Тест3":
                        sendMessage.setText("Тест3");
                        flags.set(3, true);
                        break;
                }
            }
            botTelegram.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendMsgFromCallBack(CallbackQuery callbackQuery, BotTelegram botTelegram) throws MonthException, TelegramApiException {
        messageOptions(callbackQuery.getMessage());
        editMessageOptions(callbackQuery.getMessage());
        editMessageText.setText("Выберите месяц")
                    .setReplyMarkup(chooseAnswer(callbackQuery));
        botTelegram.execute(editMessageText);
    }

    private static boolean check(ArrayList<Boolean> flags) {
        for (Boolean b : flags) {
            if (b.equals(true)) {
                return true;
            }
        }
        return false;
    }

    private static void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Погода"));
        keyboardFirstRow.add(new KeyboardButton("Календарь"));

        keyboardRows.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

    private static InlineKeyboardMarkup chooseAnswer(CallbackQuery callbackQuery) throws MonthException {
        int date,month = 0,year = 0;
        if(!callbackQuery.getData().split("'")[1].equals("")){
            date = Integer.parseInt(callbackQuery.getData().split("'")[1]);
        }
        if(!callbackQuery.getData().split("'")[2].equals("")){
            month = Integer.parseInt(callbackQuery.getData().split("'")[2]);
        }
        if(!callbackQuery.getData().split("'")[3].equals("")){
            year = Integer.parseInt(callbackQuery.getData().split("'")[3]);
        }
//                Instant.ofEpochSecond(callbackQuery.getMessage().getDate()).atZone(ZoneId.of("Europe/Moscow")).getYear();
        switch (callbackQuery.getData().split("'")[0]){
            case "ChooseMonthButton":
                return (InlineKeyboardMarkup) Calendar.createYear(year);
            case "Month":
                return (InlineKeyboardMarkup) Calendar.createMonth(month,year);
            case "Date":
                return null;
        }
        return null;
    }
}
