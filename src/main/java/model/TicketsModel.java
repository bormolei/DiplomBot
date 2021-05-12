package model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tickets")
public class TicketsModel implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_chat_id")
    private Long chatId;

    @Column(name = "departure_city")
    private String departureCity;

    @Column(name = "arrival_city")
    private String arrivalCity;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public void clearFields() {
        this.id = null;
        this.chatId = null;
        this.departureCity = null;
        this.arrivalCity = null;
        this.departureDate = null;
    }

    public void clearFieldsToDB(){
        this.departureCity = null;
        this.arrivalCity = null;
        this.departureDate = null;
    }

    public String getInfoAboutTicket() {
        String result = "";
        if (departureCity != null) {
            result += "Город отправления: " + departureCity;
        }
        if (arrivalCity != null) {
            result += "\nГород назначения: " + arrivalCity;
        }
        if (departureDate != null) {
            result += "\nДата отправления: " + departureDate;
        }
        return result;
    }
}
