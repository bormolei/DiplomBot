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
import java.util.List;

public class FileStorageMethods extends TelegramMethods {
    public static void saveFile(Message message, BotTelegram botTelegram) throws TelegramApiException {
        FileStorageModel fileStorageModel = new FileStorageModel();
        String fileId = message.getDocument().getFileId();
        byte[] res = TelegramMethods.getFile(botTelegram.getBotToken(), fileId);

        fileStorageModel.setChatId(user);
        fileStorageModel.setFile(res);
        fileStorageModel.setFileName(message.getDocument().getFileName());
        try {
            FileStorageHibernateService.addNewFile(fileStorageModel);
            sendMessage.setText("✅Ваш файл добавлен");
        } catch (Exception e) {
            sendMessage.setText("❌Ваш файл не может быть добавлен");
        }

    }

    public static void downloadFile(Integer id) {
        fileStorageModel = FileStorageHibernateService.getFile(id);
        InputStream inputStream = new ByteArrayInputStream(fileStorageModel.getFile());
        sendDocument.setDocument(fileStorageModel.getFileName(), inputStream);
    }

    public static List<FileStorageModel> getFilesFromDB() {

        return FileStorageHibernateService.getFiles(user);
    }

    public static boolean deleteFile(Integer id) {
        fileStorageModel = FileStorageHibernateService.getFile(id);
        return FileStorageHibernateService.deleteFile(fileStorageModel);
    }

}
