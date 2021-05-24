package service.Telegram;

import Exceptions.Calendar.MonthException;
import Telegram.BotTelegram;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import service.Calendar.BotCalendar;
import service.HibernateService.TicketsHibernateService;
import service.HibernateService.UserHibernateService;
import service.Tickets.TicketsMethods;
import service.Weather.WeatherBot;
import utils.Commands;

import java.time.LocalDate;

public class TelegramMethods extends TelegramService {

    public static void sendMsg(Message message, BotTelegram botTelegram) throws TelegramApiException {
        bcm.clearFields();
        user.clearFields();
        ticketsModel.clearFields();
        messageOptions(message);
        try {
            user = UserHibernateService.getUser(message.getChatId());
        } catch (IndexOutOfBoundsException e) {
            if (message.getChat().getLastName() != null) {
                user.setUserName(message.getChat().getFirstName() + " " + message.getChat().getLastName());
            } else {
                user.setUserName(message.getChat().getFirstName());
            }
            user.setChatId(message.getChatId());
            user.setMode("Greetings");
            UserHibernateService.addUser(user);
        }
        writeMsg(message, botTelegram);

    }

    private static void writeMsg(Message message, BotTelegram botTelegram) {
        try {
            setButtons(sendMessage);
            if (message.hasLocation()) {
                sendMessage.setText(weatherParser.getReadyForecast(parseGeo(message), 1))
                        .setReplyMarkup(WeatherBot.createHours(1));
            } else if (Commands.fromString(message.getText()).isPresent()) {
                chosenCommand(message);
            } else {
                switch (user.getMode().toUpperCase()) {
                    case "WEATHER":
                        TelegramMsgMethods.weatherHandler(message);
                        break;
                    case "CALENDAR":
                        TelegramMsgMethods.calendarHandler(message);
                        break;
                    case "TICKET":
                        TelegramMsgMethods.checkTicket(message);
                        TelegramMsgMethods.ticketHandler(message);
                        break;
                    case "MAIN":
                        sendMessage.setText("Выберите режим");
                        break;
                    case "GREETINGS":
                        changeModeForUser("MAIN");
                        String str = "Здравствуйте, " + user.getUserName() + ", я ваш Личный помощник. " +
                                "На данный момент я могу выполнить следующие поручения:" +
                                "\n⛅Найти прогноз погоды по городу или вашим координатам" +
                                "\n\uD83D\uDCC6Вести для вас календарь с заметками" +
                                "\n\uD83D\uDE86Найти РЖД билеты";
                        sendMessage.setText(str);
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
            case "Транспортные билеты":
                changeModeForUser(Commands.TICKET.toString());
                TelegramMsgMethods.checkTicket(message);
                ticketsModel = TicketsHibernateService.getTicketInfo(user);
                sendMessage.setText(TicketsMethods.ticketInfo(ticketsModel));
                if (TicketsMethods.hasFullInfo(ticketsModel)) {
                    TelegramMsgMethods.ticketHandler(message);
                }
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
        user = UserHibernateService.getUser(callbackQuery.getMessage().getChatId());
        messageOptions(callbackQuery.getMessage());
        editMessageOptions(callbackQuery.getMessage());
        switch (callbackQuery.getData().split("'")[0]) {
            case "Calendar":
                calendarCallBack(callbackQuery);
                break;
            case "day":
                weatherCallBack(callbackQuery);
                break;
            case "Ticket":
                ticketsCallBack(callbackQuery);
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
