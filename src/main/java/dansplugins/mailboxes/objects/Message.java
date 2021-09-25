package dansplugins.mailboxes.objects;

import java.util.Date;
import java.util.Map;

public class Message implements IMessage, Savable {

    private int ID;
    private String type;
    private String sender;
    private String recipient;
    private String content;
    private Date date;

    public Message(int ID, String type, String sender, String recipient, String content) {
        this.ID = ID;
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = new Date();
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Map<String, String> save() {
        return null;
    }

    @Override
    public void load(Map<String, String> data) {

    }
}