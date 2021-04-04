package model;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "calendar")
public class BotCalendarModel extends MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "date")
    private Date date;

    @Column(name = "task")
    private String task;

    @Column(name = "time")
    private Time time;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @Override
    public void clearFields() {
        id= null;
        chatId= null;
        date= null;
        task= null;
        time= null;
    }
}
