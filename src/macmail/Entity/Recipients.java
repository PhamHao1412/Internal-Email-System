/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail.Entity;

/**
 *
 * @author asus
 */
public class Recipients {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmail_id() {
        return email_id;
    }

    public void setEmail_id(int email_id) {
        this.email_id = email_id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getRecipient_type() {
        return recipient_type;
    }

    public void setRecipient_type(String recipient_type) {
        this.recipient_type = recipient_type;
    }
    public Recipients( int email_id, User recipient, String recipient_type) {
        this.email_id = email_id;
        this.recipient = recipient;
        this.recipient_type = recipient_type;
    }
    public Recipients(){}
    private int id;
    private int email_id;
    private User recipient;
    private String recipient_type;
}
