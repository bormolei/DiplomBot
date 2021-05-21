package service.HibernateService;

import DAO.HibernateController;
import model.City;
import model.MainModel;
import utils.Actions;

import java.util.List;

public class CitiesHibernateService {
    public static void addCity(City city){
        HibernateController.doHibernateAction(city, Actions.SAVE);
    }

//    public static List<? extends MainModel> getAllCities(){
//        HibernateController.
//    }
}
