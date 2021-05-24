package model;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class CityModel implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "city_name", unique = true)
    private String cityName;

    @Column(name = "city_code", unique = true)
    private String cityCode;

    @Column(name = "country")
    private String country;

    public Integer getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void clearFields() {
        id = null;
        cityCode = null;
        cityName = null;
        country = null;
    }
}
