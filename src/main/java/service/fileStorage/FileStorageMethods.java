package service.fileStorage;

import model.FileStorageModel;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.hibernateService.FileStorageHibernateService;
import service.telegram.TelegramMethods;
import telegram.BotTelegram;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FileStorageMethods extends TelegramMethods {
    public static void saveFile(Message message, BotTelegram botTelegram) throws TelegramApiException {
        FileStorageModel fileStorageModel = new FileStorageModel();
        String fileId = message.getDocument().getFileId();
        List<String> fileNameAndExtension = Arrays.asList(message.getDocument().getFileName().split("\\."));
        StringBuilder fileName = new StringBuilder();
        byte[] res = TelegramMethods.getFile(botTelegram.getBotToken(), fileId);
        for (int i = 0; i < fileNameAndExtension.size() - 1; i++) {
            fileName.append(fileNameAndExtension.get(i)).append(".");
        }
        fileStorageModel.setChatId(user);
        fileStorageModel.setFile(res);
        fileStorageModel.setFileName(fileName.substring(0, fileName.length() - 1));
        fileStorageModel.setFileExtension("." + fileNameAndExtension.get(fileNameAndExtension.size() - 1));
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
        sendDocument.setDocument(fileStorageModel.getFileName()+ fileStorageModel.getFileExtension(), inputStream);
    }

    public static List<FileStorageModel> getFilesFromDB() {
        return FileStorageHibernateService.getFiles(user);
    }

    public static boolean deleteFile(Integer id) {
        fileStorageModel = FileStorageHibernateService.getFile(id);
        return FileStorageHibernateService.deleteFile(fileStorageModel);
    }

//    public static List<FileStorageModel> searchFileByName(String fileName, Integer userId){
//        return FileStorageHibernateService.getFiles(user);
//    }

}
