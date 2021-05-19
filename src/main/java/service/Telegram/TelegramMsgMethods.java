package service.Telegram;

import Exceptions.Calendar.MonthException;
import org.telegram.telegrambots.meta.api.objects.Message;
import service.Calendar.BotCalendarDateConverter;
import service.Calendar.BotCalendarMethods;
import service.HibernateService.BotCalendarService;
import service.HibernateService.TicketsService;
import service.HibernateService.UserService;
import service.Tickets.TicketsMain;
import service.Tickets.TicketsMethods;
import service.Weather.WeatherBot;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class TelegramMsgMethods extends TelegramService {

    public static void checkTicket(Message message) {
        try {
            ticketsModel = TicketsService.getTicketInfo(user);
        } catch (IndexOutOfBoundsException e) {
            ticketsModel.setChatId(UserService.getUser(message.getChatId()));
            TicketsService.addNewTicket(ticketsModel);
        }
    }

    public static void ticketHandler(Message message) {
        if (!TicketsMethods.hasFullInfo(ticketsModel)) {
            String accuracy = TicketsMain.checkData(ticketsModel, message.getText());
            if (accuracy.equals("OK")) {
                TicketsMethods.addField(ticketsModel, message.getText());
                sendMessage.setText(TicketsMethods.ticketInfo(ticketsModel));
            } else {
                sendMessage.setText(accuracy);
            }
        }
        if (TicketsMethods.hasFullInfo(ticketsModel)) {
            sendMessage.setReplyMarkup(TicketsMain.getAccept());
        }
    }

    public static void calendarHandler(Message message){
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
