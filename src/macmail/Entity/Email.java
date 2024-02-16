/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail.Entity;

/**
 *
 * @author asus
 */
public class Email {
    private int id;
    private User sender;
    private User recipient;
    private String subject;
    private String content;
    private String timestamp;
    private Boolean isRead ;

    // Default constructor
    public Email() {
        // Default constructor with no parameters
    }

    // Constructor with parameters
    public Email(int id,User sender, User recipient, String subject, String content) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }
     public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
  public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    // Getter and setter methods for sender
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    // Getter and setter methods for recipient
    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    // Getter and setter methods for subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and setter methods for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter and setter methods for timestamp
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

