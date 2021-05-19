package service.HibernateService;

import DAO.HibernateController;
import model.BotCalendarModel;
import model.MainModel;
import model.User;
import utils.Actions;

import java.util.List;

public class BotCalendarService {
    public static void addTask(BotCalendarModel botCalendarModel){
        HibernateController.doHibernateAction(botCalendarModel, Actions.UPDATE);
    }

    public static List<? extends MainModel> getAllUserTasksForDay(User user){
       return HibernateController.getRowsByField(new BotCalendarModel(),"chatId", user.getId());
    }

    //Пасхалку для Вована(чотыре(4.04 4:44))
    public static void taskList(int date, int month, int year) {

    }

    public static void addPrecondition(BotCalendarModel botCalendarModel){
        HibernateController.doHibernateAction(botCalendarModel,Actions.SAVE);
    }
}
