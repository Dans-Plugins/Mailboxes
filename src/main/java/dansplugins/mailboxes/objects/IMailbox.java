package dansplugins.mailboxes.objects;

import java.util.ArrayList;
import java.util.UUID;

public interface IMailbox {
    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);
    ArrayList<Message> getMessages();
    Message getMessage(String ID);
    void addMessage(Message message);
    void removeMessage(Message message);
}