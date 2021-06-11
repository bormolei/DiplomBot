package model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "user_files")
public class FileStorageModel implements MainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_chat_id")
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User chatId;

    @Column(name = "file")
    private byte[] file;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_extension")
    private String fileExtension;


    @Override
    public void clearFields() {
        id = null;
        chatId = null;
        file = null;
        fileName = null;
        fileExtension = null;
    }

    public Integer getId() {
        return id;
    }

    public User getChatId() {
        return chatId;
    }

    public void setChatId(User chatId) {
        this.chatId = chatId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String file_extension) {
        this.fileExtension = file_extension;
    }
}
