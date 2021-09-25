package dansplugins.mailboxes.objects;

import java.util.Date;
import java.util.Map;

public class Message implements IMessage, Savable {

    @Override
    public int getID() {
        // TODO: implement
        return 0;
    }

    @Override
    public void setID(int ID) {
        // TODO: implement

    }

    @Override
    public String getSender() {
        // TODO: implement
        return null;
    }

    @Override
    public void setSender(String sender) {
        // TODO: implement
    }

    @Override
    public String getRecipient() {
        // TODO: implement
        return null;
    }

    @Override
    public void setRecipient(String recipient) {
        // TODO: implement
    }

    @Override
    public String getContent() {
        // TODO: implement
        return null;
    }

    @Override
    public void setContent(String content) {
        // TODO: implement
    }

    @Override
    public String getType() {
        // TODO: implement
        return null;
    }

    @Override
    public void setType(String type) {
        // TODO: implement
    }

    @Override
    public Date getDate() {
        // TODO: implement
        return null;
    }

    @Override
    public void setDate(Date date) {
        // TODO: implement
    }

    @Override
    public Map<String, String> save() {
        return null;
    }

    @Override
    public void load(Map<String, String> data) {

    }
}