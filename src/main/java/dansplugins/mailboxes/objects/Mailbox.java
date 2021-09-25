package dansplugins.mailboxes.objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Mailbox implements IMailbox, Savable {

    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    public UUID getOwnerUUID() {
        // TODO: implement
        return null;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        // TODO: implement
    }

    @Override
    public ArrayList<Message> getMessages() {
        // TODO: implement
        return null;
    }

    @Override
    public Message getMessage(String ID) {
        // TODO: implement
        return null;
    }

    @Override
    public void addMessage(Message message) {
        // TODO: implement
    }

    @Override
    public void removeMessage(Message message) {
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