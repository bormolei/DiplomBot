package service.hibernateService;

import DAO.HibernateController;
import model.CityModel;
import utils.Actions;

import java.util.List;

public class CitiesHibernateService {
    public static void addCity(CityModel city){
        HibernateController.doHibernateAction(city, Actions.SAVE);
    }

    public static List getAllCities(CityModel city){
        return HibernateController.getAllRows(city);
    }
}
