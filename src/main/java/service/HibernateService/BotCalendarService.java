package service.HibernateService;

import DAO.HibernateController;
import model.BotCalendarModel;
import model.MainModel;
import utils.Actions;

import java.util.List;

public class BotCalendarService {
    public static void addTask(BotCalendarModel botCalendarModel){
        HibernateController.doHibernateAction(botCalendarModel, Actions.SAVE);
    }

    public static List<? extends MainModel> getAllTasksForDayForUser(Long chatId){
       return HibernateController.getRowsByField(new BotCalendarModel(),"chatId", chatId);
    }

    //Пасхалку для Вована(чотыре(4.04 4:44))
    public static void taskList(int date, int month, int year) {

    }

    public static void addMark(BotCalendarModel botCalendarModel){

    }
}