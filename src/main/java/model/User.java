package model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "mode")
    private String mode;

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void clearFields() {
        id = null;
        userName = null;
        chatId = null;
        mode = null;
    }
}
