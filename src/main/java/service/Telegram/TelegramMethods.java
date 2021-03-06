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
import service.HibernateService.TicketsService;
import service.HibernateService.UserService;
import service.Tickets.TicketsMain;
import service.Tickets.TicketsMethods;
import service.Weather.WeatherBot;
import utils.Commands;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class TelegramMethods extends TelegramService {

    public static void sendMsg(Message message, BotTelegram botTelegram) throws TelegramApiException {
        bcm.clearFields();
        user.clearFields();
        ticketsModel.clearFields();
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
                sendMessage.setText(weatherParser.getReadyForecast(parseGeo(message), 1))
                        .setReplyMarkup(WeatherBot.createHours(1));
            } else if (Commands.fromString(message.getText()).isPresent()) {
                chosenCommand(message);
            } else {
                switch (user.getMode().toUpperCase()) {
                    case "WEATHER":
                        String weatherAnswer = weatherParser.getReadyForecast(message.getText(), 1);
                        if (weatherAnswer.contains("????????????") || weatherAnswer.contains("??????????")) {
                            sendMessage.setText(weatherAnswer);
                        } else {
                            sendMessage.setText(weatherAnswer)
                                    .setReplyMarkup(WeatherBot.createHours(1));
                        }
                        break;
                    case "CALENDAR":
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
                                sendMessage.setText("???????? ?????????????? ???? " + bcm.getDate()
                                        + " " + bcm.getTime()
                                        + " ??????????????????");
                            } catch (DateTimeParseException e) {
                                sendMessage.setText("?????????????? ?????????????? ??????????, ?????????????? ?????? ?????????????? \"11:12\"");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                sendMessage.setText("???????????????????? ???????????? ?????????? ?????? ??????????????");
                            }
                        }
                        break;
                    case "TICKET":
                        try {
                            ticketsModel = TicketsService.getTicketInfo(user.getId());
                        } catch (IndexOutOfBoundsException e) {
                            ticketsModel.setChatId(UserService.getUser(message.getChatId()));
                            TicketsService.addNewTicket(ticketsModel);
                        }
                        if (!TicketsMethods.hasFullInfo(ticketsModel)) {
                            String accuracy = TicketsMain.checkData(ticketsModel,message.getText());
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
//                        ticketWays = TicketsMain.getWay(message);
//                        sendMessage.setText(ticketWays.get(0).toString()).setReplyMarkup((ReplyKeyboard) ticketWays.get(1));
                        break;
                    case "MAIN":
                        sendMessage.setText("???????????????? ??????????");
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
            case "????????????":
                changeModeForUser(Commands.WEATHER.toString());
                setGeoLocationButton(sendMessage);
                sendMessage.setText("?????????????? ???????????????? ????????????.\n????????????????: \"????????????\" ?????? \"Moscow\"");
                break;
            case "??????????????????":
                changeModeForUser(Commands.CALENDAR.toString());
                sendMessage.setText("???????????????? ??????????")
                        .setReplyMarkup(BotCalendar.createMonth(currentMonth, LocalDate.now().getYear()));
                break;
            case "???????????????????????? ????????????":
                changeModeForUser(Commands.TICKET.toString());
                sendMessage.setText("???????????????? \"???????????? ?????????? "
                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        + "\" ?????????? ???????????? ?? ???????????????? ?????????????????? ??????????????????????.");
                break;
            case "???? ??????????????":
                changeModeForUser(Commands.Main.toString());
                sendMessage.setText("???????????????? ??????????");
                break;
        }
    }

    public static void sendMsgFromCallBack(CallbackQuery callbackQuery, BotTelegram botTelegram) throws TelegramApiException, MonthException {
        bcm.clearFields();
        user.clearFields();
        user = UserService.getUser(callbackQuery.getMessage().getChatId());
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
