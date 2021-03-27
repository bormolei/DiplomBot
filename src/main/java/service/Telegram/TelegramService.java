package service.Telegram;

import Exceptions.Calendar.MonthException;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import service.Calendar.Calendar;
import service.HibernateService.UserService;
import service.Weather.WeatherBot;
import service.Weather.WeatherParser;

public class TelegramService {
    protected static User user = new User();
    protected static WeatherParser weatherParser = new WeatherBot();
    protected static SendMessage sendMessage;
    protected static EditMessageText editMessageText;
    protected static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    public static String checkMode(Long chatId){
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

    protected static void changeModeForUser(String mode, Long chatId) {
        user = UserService.getUser(chatId);
        if(!(user.getMode() == null || user.getMode().equals(mode))){
            user.setMode(mode);
            UserService.updateUser(user);
        }
    }

    protected static String parceGeo(Message message) {
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder("ac2f76c5d0f049db9a15d712ad3db49c");
        JOpenCageReverseRequest request = new JOpenCageReverseRequest(message.getLocation().getLatitude().doubleValue(), message.getLocation().getLongitude().doubleValue());
        request.setLanguage("ru");
        request.setNoDedupe(true);
        request.setNoAnnotations(true);
        request.setMinConfidence(3);
        JOpenCageResponse response = jOpenCageGeocoder.reverse(request);
        return response.getResults().get(0).getFormatted().split(", ")[2];
    }

    protected static InlineKeyboardMarkup chooseAnswer(CallbackQuery callbackQuery) throws MonthException {
        int date, month = 0, year = 0;
        if (!callbackQuery.getData().split("'")[1].equals("")) {
            date = Integer.parseInt(callbackQuery.getData().split("'")[1]);
        }
        if (!callbackQuery.getData().split("'")[2].equals("")) {
            month = Integer.parseInt(callbackQuery.getData().split("'")[2]);
        }
        if (!callbackQuery.getData().split("'")[3].equals("")) {
            year = Integer.parseInt(callbackQuery.getData().split("'")[3]);
        }
        switch (callbackQuery.getData().split("'")[0]) {
            case "ChooseMonthButton":
                return (InlineKeyboardMarkup) Calendar.createYear(year);
            case "Month":
                return (InlineKeyboardMarkup) Calendar.createMonth(month, year);
            case "Date":
                return null;
        }
        return null;
    }
}
