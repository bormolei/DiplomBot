package service.Telegram;

import Exceptions.Calendar.MonthException;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import model.BotCalendarModel;
import model.MainModel;
import model.TicketsModel;
import model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import service.Calendar.BotCalendar;
import service.Calendar.BotCalendarDateConverter;
import service.HibernateService.BotCalendarService;
import service.HibernateService.TicketsService;
import service.HibernateService.UserService;
import service.Tickets.TicketsMain;
import service.Tickets.TicketsMethods;
import service.Weather.WeatherBot;
import service.Weather.WeatherParser;
import utils.Commands;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TelegramService {
    protected static BotCalendarModel bcm = new BotCalendarModel();
    protected static User user = new User();
    protected static TicketsModel ticketsModel = new TicketsModel();
    protected static WeatherParser weatherParser = new WeatherBot();
    protected static SendMessage sendMessage;
    protected static EditMessageText editMessageText;
    protected static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    protected static List ticketWays = new ArrayList();


    public static String checkMode(Long chatId) {
        return UserService.getMode(chatId);
    }

    protected static void messageOptions(Message message) {
        sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
    }

    protected static void editMessageOptions(Message message) {
        editMessageText = new EditMessageText();
        editMessageText.setChatId(message.getChatId());
        editMessageText.setMessageId(message.getMessageId());
    }

    protected static void changeModeForUser(String mode) {
        if (!(user.getMode() == null || user.getMode().equals(mode))) {
            user.setMode(mode);
            UserService.updateUser(user);
        }
    }

    protected static String parseGeo(Message message) {
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("ac2f76c5d0f049db9a15d712ad3db49c");
        JOpenCageReverseRequest request = new JOpenCageReverseRequest(message.getLocation().getLatitude().doubleValue(), message.getLocation().getLongitude().doubleValue());
        request.setLanguage("ru");
        request.setNoDedupe(true);
        request.setNoAnnotations(true);
        request.setMinConfidence(3);
        JOpenCageResponse response = jOpenCageGeocoder.reverse(request);
        return response.getResults().get(0).getComponents().getCity();
    }

    protected static InlineKeyboardMarkup chooseAnswer(CallbackQuery callbackQuery) throws MonthException {
        int date, month = 0, year = 0;
        if (!callbackQuery.getData().split("'")[2].equals("")) {
            date = Integer.parseInt(callbackQuery.getData().split("'")[2]);
        }
        if (!callbackQuery.getData().split("'")[3].equals("")) {
            month = Integer.parseInt(callbackQuery.getData().split("'")[3]);
        }
        if (!callbackQuery.getData().split("'")[4].equals("")) {
            year = Integer.parseInt(callbackQuery.getData().split("'")[4]);
        }
        switch (callbackQuery.getData().split("'")[1]) {
            case "ChooseMonthButton":
                return (InlineKeyboardMarkup) BotCalendar.createYear(year);
            case "Month":
                return (InlineKeyboardMarkup) BotCalendar.createMonth(month, year);
        }
        return null;
    }

    protected static void setButtons(SendMessage sendMessage) {
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

    protected static void setGeoLocationButton(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Отправить свое местоположение").setRequestLocation(true));
        keyboardFirstRow.add(new KeyboardButton("На главную"));

        keyboardRows.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

    protected static void setNewTaskForDay(CallbackQuery callbackQuery) throws ParseException {
        Long chatId = callbackQuery.getMessage().getChatId();
        LocalDate ld = (BotCalendarDateConverter.fromStringToDate(callbackQuery.getData().split("'")[2]));
        List<? extends MainModel> userDays = BotCalendarService.getAllUserTasksForDay(chatId);
        int bcmNumber = BotCalendar.hasUserDay(userDays, ld);
        if (bcmNumber != -1) {
            bcm = (BotCalendarModel) userDays.get(bcmNumber);
        } else {
            bcm.setChatId(chatId);
            bcm.setDate(ld);
        }
        bcm.setAddUpdFlag(true);
    }

    private static KeyboardButton backToMain() {
        return new KeyboardButton("На главную");
    }

    private static String getUserDay(LocalDate date, List<? extends MainModel> userDays) {
        for (MainModel userDay : userDays) {
            bcm = (BotCalendarModel) userDay;
            if (bcm.getDate().equals(date)) {
                return bcm.getTask();
            }
        }
        return "Запланированных дел нет";
    }

    protected static void calendarCallBack(CallbackQuery callbackQuery) throws MonthException {
        if (callbackQuery.getData().split("'")[1].equals("date")) {
            int date = Integer.parseInt(callbackQuery.getData().split("'")[2]);
            int month = Integer.parseInt(callbackQuery.getData().split("'")[3]);
            int year = Integer.parseInt(callbackQuery.getData().split("'")[4]);
            LocalDate ld = LocalDate.of(year, month, date);
            String tasks = getUserDay(ld, BotCalendarService.getAllUserTasksForDay(callbackQuery.getMessage().getChatId()));
            editMessageText.setText(String.format("Запланированные дела на %s-%s-%s\n" + tasks, date, month, year))
                    .setReplyMarkup((InlineKeyboardMarkup) BotCalendar.taskList(date, month, year));
        } else if (callbackQuery.getData().split("'")[1].equals("add")) {
            try {
                setNewTaskForDay(callbackQuery);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            BotCalendarService.addPrecondition(bcm);
            editMessageText.setText("Укажите время для вашей заметки и текст заметки" +
                    "\nобразец \"11:12-Прогулка\"");
        } else {
            editMessageText.setText("Выберите месяц")
                    .setReplyMarkup(chooseAnswer(callbackQuery));
        }
    }

    protected static void weatherCallBack(CallbackQuery callbackQuery) throws MonthException {
        if (callbackQuery.getData().split("'")[0].equals("day")) {
            String city = callbackQuery.getMessage().getText().split(":")[0];
            int days = Integer.parseInt(callbackQuery.getData().split("'")[1]);
            editMessageText.setText(weatherParser.getReadyForecast(city, days))
                    .setReplyMarkup((InlineKeyboardMarkup) WeatherBot.createHours(days));

        }
    }

    protected static void ticketsCallBack(CallbackQuery callbackQuery) {
        ticketsModel = TicketsService.getTicketInfo(callbackQuery.getMessage().getChatId());
        if (callbackQuery.getData().split("'")[1].equals("get")) {
            String from = TicketsMain.getTicketInfo(ticketsModel.getDepartureCity());
            String to = TicketsMain.getTicketInfo(ticketsModel.getArrivalCity());
            String date = ticketsModel.getDepartureDate().toString();
            editMessageText.setText(ticketsModel.getInfoAboutTicket())
                    .setReplyMarkup(TicketsMain.getRzdURI(from, to, date, 0));
            ticketsModel.clearFieldsToDB();
            changeModeForUser(Commands.Main.toString());
        } else if(callbackQuery.getData().split("'")[1].equals("cancel")){
            ticketsModel.clearFieldsToDB();
            editMessageText.setText(TicketsMethods.ticketInfo(ticketsModel));
        }
        TicketsService.updateTicketInfo(ticketsModel);
    }

    protected static void backToMainMenu(CallbackQuery callbackQuery) {
        setButtons(sendMessage);
        sendMessage.setText("Выберите режим");
    }
}
