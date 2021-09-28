package dansplugins.mailboxes.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public interface IMailbox {
    int getID();
    void setID(int ID);
    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);
    ArrayList<Message> getMessages();
    Message getMessage(int ID);
    void addMessage(Message message);
    void removeMessage(Message message);
    void removeMessage(int ID);
    void sendListOfMessagesToPlayer(Player player);
}