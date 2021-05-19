package model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tickets")
public class TicketsModel implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_chat_id", unique = true)
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User chatId;

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

    public User getChatId() {
        return chatId;
    }

    public void setChatId(User chatId) {
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
