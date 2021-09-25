package dansplugins.mailboxes.objects;

import java.util.Date;

public interface IMessage {
    int getID();
    void setID(int ID);
    String getSender();
    void setSender(String sender);
    String getRecipient();
    void setRecipient(String recipient);
    String getContent();
    void setContent(String content);
    String getType();
    void setType(String type);
    Date getDate();
    void setDate(Date date);
}