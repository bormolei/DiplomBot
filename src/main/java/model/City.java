package model;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "city_code")
    private Long cityCode;

    public Integer getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getCityCode() {
        return cityCode;
    }

    public void setCityCode(Long cityCode) {
        this.cityCode = cityCode;
    }

    public void clearFields() {
        id = null;
        cityName = null;
        cityCode = null;
    }
}
