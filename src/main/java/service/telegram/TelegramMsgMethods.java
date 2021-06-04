package service.telegram;

import Exceptions.Calendar.MonthException;
import org.telegram.telegrambots.meta.api.objects.Message;
import service.calendar.BotCalendarDateConverter;
import service.calendar.BotCalendarMethods;
import service.exchangeRates.ExchangeRates;
import service.hibernateService.BotCalendarHibernateService;
import service.hibernateService.TicketsHibernateService;
import service.hibernateService.UserHibernateService;
import service.tickets.TicketsMain;
import service.tickets.TicketsMethods;
import service.weather.WeatherBot;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TelegramMsgMethods extends TelegramMethods {

    public static void checkTicket(Message message) {
        try {
            ticketsModel = TicketsHibernateService.getTicketInfo(user);
        } catch (IndexOutOfBoundsException e) {
            ticketsModel.setChatId(UserHibernateService.getUser(message.getChatId()));
            TicketsHibernateService.addNewTicket(ticketsModel);
        }
    }

    public static void ticketHandler(Message message) {
        if (!TicketsMethods.hasFullInfo(ticketsModel)) {
            String city = message.getText().substring(0, 1).toUpperCase(Locale.ROOT) + message.getText().substring(1).toLowerCase();
            String accuracy = TicketsMain.checkData(ticketsModel, city.toLowerCase(Locale.ROOT));
            if (accuracy.equals("OK")) {
                TicketsMethods.addField(ticketsModel, city);
                sendMessage.setText(TicketsMethods.ticketInfo(ticketsModel));
            } else {
                sendMessage.setText(accuracy);
            }
        }
        if (TicketsMethods.hasFullInfo(ticketsModel)) {
            sendMessage.setReplyMarkup(TicketsMain.getAccept());
        }
    }

    public static void exchangeRates(Message message) {
        try {

            Double amount = Double.parseDouble(message.getText().replace(",","."));
            sendMessage.setText("Укажите к какой валюте произвести расчет")
                    .setReplyMarkup(ExchangeRates.setCurries(amount));
        } catch (NumberFormatException e){
            sendMessage.setText("Не корректное значение");
        }
    }

    public static void calendarHandler(Message message) {
        bcm = BotCalendarMethods.readyForTask(user);
        if (bcm != null) {
            List<String> userMessage = Arrays.asList(message.getText().split("-"));
            try {
                bcm.setTime(BotCalendarDateConverter.parseTime(userMessage.get(0)));
                String tasks = bcm.getTask();
                if (tasks == null) {
                    tasks = userMessage.get(1);
                } else {
                    tasks += "\n" + userMessage.get(1);
                }
                bcm.setTask(tasks);
                bcm.setAddUpdFlag(false);
                BotCalendarHibernateService.addTask(bcm);
                sendMessage.setText("Ваша заметка на " + bcm.getDate()
                        + " " + bcm.getTime()
                        + " добавлена");
            } catch (DateTimeParseException e) {
                sendMessage.setText("Неверно введено время, образец для времени \"11:12\"");
            } catch (ArrayIndexOutOfBoundsException e) {
                sendMessage.setText("Необходимо ввести текст для заметки");
            }
        }
    }

    public static void weatherHandler(Message message) throws MonthException {
        String weatherAnswer = weatherParser.getReadyForecast(message.getText(), 1);
        if (weatherAnswer.contains("Сервис") || weatherAnswer.contains("режим")) {
            sendMessage.setText(weatherAnswer);
        } else {
            sendMessage.setText(weatherAnswer)
                    .setReplyMarkup(WeatherBot.createHours(1));
        }
    }

}
