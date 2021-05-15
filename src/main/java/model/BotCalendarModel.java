package model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "calendar")
public class BotCalendarModel implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "chat_id")
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User chatId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "task")
    private String task;

    @Column(name = "addupd")
    private Boolean addUpdFlag;

    public User getChatId() {
        return chatId;
    }

    public void setChatId(User chatId) {
        this.chatId = chatId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Boolean getAddUpdFlag() {
        return addUpdFlag;
    }

    public void setAddUpdFlag(Boolean addUpdFlag) {
        this.addUpdFlag = addUpdFlag;
    }

    @Override
    public void clearFields() {
        id = null;
        chatId = null;
        date = null;
        task = null;
        time = null;
        addUpdFlag = null;
    }
}
