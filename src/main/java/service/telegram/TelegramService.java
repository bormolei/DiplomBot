package service.telegram;

import Exceptions.Calendar.MonthException;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import io.restassured.RestAssured;
import model.*;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import service.calendar.BotCalendar;
import service.calendar.BotCalendarDateConverter;
import service.hibernateService.BotCalendarHibernateService;
import service.hibernateService.TicketsHibernateService;
import service.hibernateService.UserHibernateService;
import service.tickets.TicketsMain;
import service.tickets.TicketsMethods;
import service.weather.WeatherBot;
import service.weather.WeatherParser;
import utils.Commands;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TelegramService {
    protected static BotCalendarModel bcm = new BotCalendarModel();
    protected static User user = new User();
    protected static TicketsModel ticketsModel = new TicketsModel();
    protected static FileStorageModel fileStorageModel = new FileStorageModel();
    protected static WeatherBot weatherParser = new WeatherBot();
    protected static SendMessage sendMessage;
    protected static EditMessageText editMessageText;
    protected static SendDocument document;
    protected static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    static final String baseUri = "https://api.telegram.org";

    protected static String secretKey = "U2WoExDA1R";
    protected static String salt = "k3G6b8w0V8";



    public static String checkMode(Long chatId) {
        return UserHibernateService.getMode(chatId);
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

    protected static void documentOptions(Message message) {
        document = new SendDocument();
        document.setChatId(message.getChatId());
    }

    protected static void changeModeForUser(String mode) {
        if (!(user.getMode() == null || user.getMode().equals(mode))) {
            user.setMode(mode);
            UserHibernateService.updateUser(user);
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
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Погода"));
        keyboardFirstRow.add(new KeyboardButton("Календарь"));
        keyboardSecondRow.add(new KeyboardButton("Транспортные билеты"));
        keyboardSecondRow.add(new KeyboardButton("Мои файлы"));
        keyboardThirdRow.add(new KeyboardButton("Курс валют"));

        keyboardRows.add(keyboardFirstRow);
        keyboardRows.add(keyboardSecondRow);
        keyboardRows.add(keyboardThirdRow);
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
        List<? extends MainModel> userDays = BotCalendarHibernateService.getAllUserTasksForDay(user);
        int bcmNumber = BotCalendar.hasUserDay(userDays, ld);
        if (bcmNumber != -1) {
            bcm = (BotCalendarModel) userDays.get(bcmNumber);
        } else {
            bcm.setChatId(UserHibernateService.getUser(callbackQuery.getMessage().getChatId()));
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
            String tasks = getUserDay(ld, BotCalendarHibernateService.getAllUserTasksForDay(user));
            editMessageText.setText(String.format("Запланированные дела на %s-%s-%s\n" + tasks, date, month, year))
                    .setReplyMarkup((InlineKeyboardMarkup) BotCalendar.taskList(date, month, year));
        } else if (callbackQuery.getData().split("'")[1].equals("add")) {
            try {
                setNewTaskForDay(callbackQuery);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            BotCalendarHibernateService.addPrecondition(bcm);
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
        ticketsModel = TicketsHibernateService.getTicketInfo(user);
        if (callbackQuery.getData().split("'")[1].equals("get")) {
            String from = TicketsMain.getTicketInfo(ticketsModel.getDepartureCity());
            String to = TicketsMain.getTicketInfo(ticketsModel.getArrivalCity());
            String date = ticketsModel.getDepartureDate().toString();
            InlineKeyboardMarkup inlineKeyboardMarkup = TicketsMain.getRzdURI(from, to, date, 0);
            if (inlineKeyboardMarkup.getKeyboard().size() != 0) {
                editMessageText.setText(ticketsModel.getInfoAboutTicket())
                        .setReplyMarkup(inlineKeyboardMarkup);
            } else {
                editMessageText.setText(ticketsModel.getInfoAboutTicket() + "\n\nПрямые маршруты по данном " +
                        "направлению или дате отсутсвуют");
            }
            ticketsModel.clearFieldsToDB();
            changeModeForUser(Commands.Main.toString());
        } else if (callbackQuery.getData().split("'")[1].equals("cancel")) {
            ticketsModel.clearFieldsToDB();
            editMessageText.setText(TicketsMethods.ticketInfo(ticketsModel));
        }
        TicketsHibernateService.updateTicketInfo(ticketsModel);
    }

    protected static void backToMainMenu(CallbackQuery callbackQuery) {
        setButtons(sendMessage);
        sendMessage.setText("Выберите режим");
    }

    protected static String getFilePath(String botToken, String fileId) {
        String query = baseUri + "/bot" + botToken + "/getFile?file_id=" + fileId;

        String response = RestAssured.given()
                .get(query)
                .getBody()
                .asString();

        return new JSONObject(response)
                .getJSONObject("result")
                .getString("file_path");
    }
}
