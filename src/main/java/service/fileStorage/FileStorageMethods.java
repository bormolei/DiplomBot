package service.fileStorage;

import model.FileStorageModel;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.hibernateService.FileStorageHibernateService;
import service.hibernateService.UserHibernateService;
import service.telegram.TelegramMethods;
import telegram.BotTelegram;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileStorageMethods extends TelegramMethods {
    public static void saveFile(Message message, BotTelegram botTelegram) throws TelegramApiException {
        FileStorageModel fileStorageModel = new FileStorageModel();
        String fileId = message.getDocument().getFileId();
        byte[] res = TelegramMethods.getFile(botTelegram.getBotToken(), fileId);

        fileStorageModel.setChatId(UserHibernateService.getUser(message.getChatId()));
        fileStorageModel.setFile(res);
        fileStorageModel.setFileName(message.getDocument().getFileName());
        try {
            FileStorageHibernateService.addNewFile(fileStorageModel);
            sendMessage.setText("Ваш файл добавлен");
        } catch (Exception e) {
            sendMessage.setText("Ваш файл не может быть добавлен");
        }

    }

    public static void downloadFile(Integer id) {
        fileStorageModel = FileStorageHibernateService.downloadFile(id);
        InputStream inputStream = new ByteArrayInputStream(fileStorageModel.getFile());
        document.setDocument(fileStorageModel.getFileName(),inputStream);
    }

    public static List<FileStorageModel> getFileFromDB(Message message) {
        return FileStorageHibernateService.getFiles(UserHibernateService.getUser(message.getChatId()));
    }

}
