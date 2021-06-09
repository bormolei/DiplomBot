package service.telegram;

import Exceptions.Calendar.MonthException;
import io.restassured.RestAssured;
import model.FileStorageModel;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import service.exchangeRates.ExchangeRates;
import service.fileStorage.FileKeyboard;
import service.fileStorage.FileStorageMethods;
import telegram.BotTelegram;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import service.calendar.BotCalendar;
import service.hibernateService.TicketsHibernateService;
import service.hibernateService.UserHibernateService;
import service.tickets.TicketsMethods;
import service.weather.WeatherBot;
import utils.Commands;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

public class TelegramMethods extends TelegramService {

    public static void getMsg(Message message, BotTelegram botTelegram) throws TelegramApiException {
        bcm.clearFields();
        user.clearFields();
        ticketsModel.clearFields();
        fileStorageModel.clearFields();
        messageOptions(message);
        documentOptions(message);
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
            } else if (message.hasDocument()) {
                FileStorageMethods.saveFile(message, botTelegram);
            } else if (message.hasPhoto()) {
                sendMessage.setText("Будьте добры, отправьте ваш файл в виде документа");
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
                    case "EXCHANGERATES":
                        TelegramMsgMethods.exchangeRates(message);
                        break;
                    case "GREETINGS":
                        changeModeForUser("MAIN");
                        String str = "Здравствуйте, " + user.getUserName() + ", я ваш Личный помощник. " +
                                "На данный момент я могу выполнить следующие поручения:" +
                                "\n⛅Найти прогноз погоды по городу или вашим координатам." +
                                "\n\uD83D\uDCC6Вести для вас календарь с заметками." +
                                "\n\uD83D\uDCC2Хранилище файлов." +
                                "\n\uD83D\uDE86Найти РЖД билеты." +
                                "\n\uD83D\uDCB5Курс и конвертер валюты.";
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
                sendMessage.setText("Введите название города.\nНапример: \uD83C\uDDF7\uD83C\uDDFA\"Москва\" или \uD83C\uDDFA\uD83C\uDDF8\"Moscow\"");
                break;
            case "Календарь":
                changeModeForUser(Commands.CALENDAR.toString());
                sendMessage.setText("Выберите число или смените месяц")
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
            case "Мои файлы":
                sendMessage.setText("Выберите что вы хотите сделать с вашими файлами").setReplyMarkup(FileKeyboard.chooseFileMode());
                break;
            case "Курс валют":
                changeModeForUser(Commands.EXCHANGERATES.toString());
                sendMessage.setText("Введите значение которое необходимо конвертировать");
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
        documentOptions(callbackQuery.getMessage());
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
            case "File":
                fileStorageCallBack(callbackQuery);
                break;
            case "Rates":
                String result = ExchangeRates.convert(callbackQuery);
                if (result != null) {
                    editMessageText.setText(result)
                            .setReplyMarkup((InlineKeyboardMarkup) ExchangeRates
                                    .setCurries(Double
                                            .parseDouble(callbackQuery.getData().split("'")[2])));
                } else {
                    editMessageText.setText("Данное число нельзя сконвертировать");
                }
                break;
            case "MainMenu":
                changeModeForUser(Commands.Main.toString());
                backToMainMenu(callbackQuery);
                break;
        }
        try {
            if (sendMessage.getText() != null) {
                botTelegram.execute(sendMessage);
            }
            if (!callbackQuery.getData().split("'")[0].equals(" ") && editMessageText.getText() != null) {
                botTelegram.execute(editMessageText);
            }
            if (sendDocument.getDocument() != null) {
                botTelegram.execute(sendDocument);
            }
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getFile(String botToken, String fileId) {
        String query = baseUri + "/file/bot" + botToken + "/" + getFilePath(botToken, fileId);
        return RestAssured.given()
                .get(query)
                .asByteArray();
    }

    public static String encrypt(String word) {

        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(word.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String word) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(word)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}
