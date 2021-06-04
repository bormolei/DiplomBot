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

    public static boolean haventCities(){
        return HibernateController.getAllRows(new CityModel()).isEmpty();
    }

    public static boolean haveCity(String cityName){
        return !HibernateController.getRowsByField(new CityModel(), "cityName", cityName).isEmpty();
    }

    public static CityModel getCity(String cityCode){
        return (CityModel) HibernateController.getRowsByField(new CityModel(),"cityCode", cityCode).get(0);
    }
}
