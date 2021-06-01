package service.fileStorage;

import model.FileStorageModel;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.telegram.TelegramKeyboard;

import java.util.ArrayList;
import java.util.List;

public class FileKeyboard extends TelegramKeyboard {
    static List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
    public static ReplyKeyboard createFileKeyboard(List<FileStorageModel> fileNames){
        rowList.clear();
        TelegramKeyboard.clearKeyBoard();
        for (int i = 0; i < fileNames.size(); i++) {
            FileStorageModel fileStorageModel = fileNames.get(i);
            if(i<2){
                keyboardButtonsRow1.add(new InlineKeyboardButton().setText(fileStorageModel.getFileName())
                        .setCallbackData("File'"+fileStorageModel.getId()));
            } else if(i<4){
                keyboardButtonsRow2.add(new InlineKeyboardButton().setText(fileStorageModel.getFileName())
                        .setCallbackData("File'"+fileStorageModel.getId()));
            } else if(i<6){
                keyboardButtonsRow3.add(new InlineKeyboardButton().setText(fileStorageModel.getFileName())
                        .setCallbackData("File'"+fileStorageModel.getId()));
            }
        }
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
