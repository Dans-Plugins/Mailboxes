package dansplugins.mailboxes.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public interface IMailbox {
    int getID();
    void setID(int ID);
    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);
    ArrayList<Message> getActiveMessages();
    Message getActiveMessage(int ID);
    void addActiveMessage(Message message);
    void removeActiveMessage(Message message);
    void removeActiveMessage(int ID);
    void sendListOfActiveMessagesToPlayer(Player player);
    void archiveMessage(Message message);
}