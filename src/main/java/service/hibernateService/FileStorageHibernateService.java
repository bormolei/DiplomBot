package service.hibernateService;

import DAO.HibernateController;
import model.FileStorageModel;
import model.MainModel;
import model.User;
import utils.Actions;

import java.util.List;

public class FileStorageHibernateService {
    static FileStorageModel file = new FileStorageModel();

    public static void addNewFile(FileStorageModel file) {
        HibernateController.doHibernateAction(file, Actions.SAVE);
    }

    public static List<FileStorageModel> getFiles(User user) {
        return (List<FileStorageModel>) HibernateController.getRowsByField(file, "chatId", user.getId());
    }

    public static FileStorageModel downloadFile(int id) {
        return (FileStorageModel) HibernateController.getRowsByField(file, "id", id).get(0);
    }
}
