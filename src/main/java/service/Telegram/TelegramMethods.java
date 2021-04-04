package service.Telegram;

import Exceptions.Calendar.MonthException;
import Telegram.BotTelegram;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.Calendar.BotCalendar;
import service.Calendar.BotCalendarDateConverter;
import service.HibernateService.BotCalendarService;
import service.HibernateService.UserService;
import utils.Commands;

import java.text.ParseException;
import java.time.LocalDate;

public class TelegramMethods extends TelegramService {

    public static void sendMsg(Message message, BotTelegram botTelegram) throws TelegramApiException {
        btm.clearFields();
        user.clearFields();
        try {
            user = UserService.getUser(message.getChatId());
        } catch (IndexOutOfBoundsException e) {
            user.setUserName(message.getChat().getFirstName() + " " + message.getChat().getLastName());
            user.setChatId(message.getChatId());
            user.setMode("");
            UserService.addUser(user);
        }
        messageOptions(message);
        writeMsg(message, botTelegram);

    }

    private static void writeMsg(Message message, BotTelegram botTelegram) {
        try {
            setButtons(sendMessage);
            if (message.hasLocation()) {
                sendMessage.setText(weatherParser.getReadyForecast(parceGeo(message)));
            } else if (Commands.fromString(message.getText()).isPresent()) {
                chosenCommand(message);
            } else {
                switch (user.getMode()) {
                    case "WEATHER":
                        sendMessage.setText(weatherParser.getReadyForecast(message.getText()));
                        break;
                    case "CALENDAR":
//                        calendar.setTimeInMillis(message.getDate().longValue()*1000);
//                        sendMessage.setText(sdf.format(calendar.getTime()));
                        message.getText();
                        break;
                    default:
                        sendMessage.setText("Выберите режим");
                        break;
                }

            }
            botTelegram.execute(sendMessage);
        } catch (TelegramApiException | MonthException e) {
            e.printStackTrace();
        }
    }


    private static void chosenCommand(Message message) throws MonthException {
        int currentMonth = LocalDate.now().getMonth().getValue();
        switch (message.getText()) {
            case "Погода":
                changeModeForUser(Commands.WEATHER.toString());
                setGeoLocationButton(sendMessage);
                sendMessage.setText("Введите название города.Например: \"Москва\" или \"Moscow\"");
                break;
            case "Календарь":
                changeModeForUser(Commands.CALENDAR.toString());
                sendMessage.setText("Выберите число")
                        .setReplyMarkup(BotCalendar.createMonth(currentMonth, LocalDate.now().getYear()));
                break;
            case "Тест3":
                sendMessage.setText("Тест3");
                break;
        }
    }

    public static void sendMsgFromCallBack(CallbackQuery callbackQuery, BotTelegram botTelegram) throws TelegramApiException, MonthException {
        messageOptions(callbackQuery.getMessage());
        editMessageOptions(callbackQuery.getMessage());
        if (callbackQuery.getData().split("'")[0].equals("date")) {
            int date = Integer.parseInt(callbackQuery.getData().split("'")[1]);
            int month = Integer.parseInt(callbackQuery.getData().split("'")[2]);
            int year = Integer.parseInt(callbackQuery.getData().split("'")[3]);
            editMessageText.setText(String.format("Запланированные дела на %s-%s-%s\n", date,month,year))
                    .setReplyMarkup((InlineKeyboardMarkup) BotCalendar.taskList(date,month,year));
        } else if(callbackQuery.getData().split("'")[0].equals("add")) {
            try {
                btm.setDate(BotCalendarDateConverter.fromStringToDate(callbackQuery.getData().split("'")[1]));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            BotCalendarService.addMark(btm);
            editMessageText.setText("Введите заметку" +
                    "\nобразец \"13:24-Помыть посуду, позвонить, ....\"");
        } else {
            editMessageText.setText("Выберите месяц")
                    .setReplyMarkup(chooseAnswer(callbackQuery));
        }
        botTelegram.execute(editMessageText);
    }


}
