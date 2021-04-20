package service.Telegram;

import Exceptions.Calendar.MonthException;
import Telegram.BotTelegram;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import service.Calendar.BotCalendar;
import service.Calendar.BotCalendarDateConverter;
import service.Calendar.BotCalendarMethods;
import service.HibernateService.BotCalendarService;
import service.HibernateService.UserService;
import service.Weather.WeatherBot;
import utils.Commands;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class TelegramMethods extends TelegramService {

    public static void sendMsg(Message message, BotTelegram botTelegram) throws TelegramApiException {
        bcm.clearFields();
        user.clearFields();
        try {
            user = UserService.getUser(message.getChatId());
        } catch (IndexOutOfBoundsException e) {
            user.setUserName(message.getChat().getFirstName() + " " + message.getChat().getLastName());
            user.setChatId(message.getChatId());
            user.setMode("Main");
            UserService.addUser(user);
        }
        messageOptions(message);
        writeMsg(message, botTelegram);

    }

    private static void writeMsg(Message message, BotTelegram botTelegram) {
        try {
            setButtons(sendMessage);
            if (message.hasLocation()) {
                sendMessage.setText(weatherParser.getReadyForecast(parceGeo(message), 1))
                        .setReplyMarkup(WeatherBot.createHours(1));
            } else if (Commands.fromString(message.getText()).isPresent()) {
                chosenCommand(message);
            } else {
                switch (user.getMode()) {
                    case "WEATHER":
                        String weatherAnswer = weatherParser.getReadyForecast(message.getText(), 1);
                        if (weatherAnswer.contains("Сервис") || weatherAnswer.contains("режим")) {
                            sendMessage.setText(weatherAnswer);
                        } else {
                            sendMessage.setText(weatherAnswer)
                                    .setReplyMarkup(WeatherBot.createHours(1));
                        }
                        break;
                    case "CALENDAR":
                        bcm = BotCalendarMethods.readyForTask(message.getChatId());
                        if (bcm != null) {
                            List<String> userMessage = Arrays.asList(message.getText().split("-"));
                            try {
                                bcm.setTime(BotCalendarDateConverter.parceTime(userMessage.get(0)));
                                String tasks = bcm.getTask();
                                tasks+="\n"+userMessage.get(1);
                                bcm.setTask(tasks);
                                bcm.setAddUpdFlag(false);
                                BotCalendarService.addTask(bcm);
                                sendMessage.setText("Ваша заметка на " + bcm.getDate()
                                        + " " + bcm.getTime()
                                        + " добавлена");
                            } catch (DateTimeParseException e) {
                                sendMessage.setText("Неверно введено время, образец для времени \"11:12\"");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                sendMessage.setText("Необходимо ввести текст для заметки");
                            }
                        }
                        break;
                    case "Main":
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
                sendMessage.setText("Введите название города.\nНапример: \"Москва\" или \"Moscow\"");
                break;
            case "Календарь":
                changeModeForUser(Commands.CALENDAR.toString());
                sendMessage.setText("Выберите число")
                        .setReplyMarkup(BotCalendar.createMonth(currentMonth, LocalDate.now().getYear()));
                break;
            case "На главную":
                changeModeForUser(Commands.Main.toString());
                sendMessage.setText("Выберите режим");
                break;
        }
    }

    public static void sendMsgFromCallBack(CallbackQuery callbackQuery, BotTelegram botTelegram) throws TelegramApiException, MonthException {
        bcm.clearFields();
        user.clearFields();
        messageOptions(callbackQuery.getMessage());
        editMessageOptions(callbackQuery.getMessage());
        switch (callbackQuery.getData().split("'")[0]) {
            case "Calendar":
                calendarCallBack(callbackQuery);
                break;
            case "day":
                weatherCallBack(callbackQuery);
                break;
            case "MainMenu":
                changeModeForUser(Commands.Main.toString());
                backToMainMenu(callbackQuery);
                botTelegram.execute(sendMessage);
                break;
        }
        try {
            if (!callbackQuery.getData().split("'")[0].equals(" ") && editMessageText.getText() != null) {
                botTelegram.execute(editMessageText);
            }
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


}
