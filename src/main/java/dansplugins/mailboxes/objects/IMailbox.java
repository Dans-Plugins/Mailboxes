package dansplugins.mailboxes.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public interface IMailbox {
    int getID();
    void setID(int ID);
    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);
    Message getMessage(int ID);
    void removeMessage(Message message);
    ArrayList<Message> getActiveMessages();
    Message getActiveMessage(int ID);
    void addActiveMessage(Message message);
    void removeActiveMessage(Message message);
    void removeActiveMessage(int ID);
    void sendListOfActiveMessagesToPlayer(Player player);
    ArrayList<Message> getArchivedMessages();
    Message getArchivedMessage(int ID);
    void addArchivedMessage(Message message);
    void removeArchivedMessage(Message message);
    void removeArchivedMessage(int ID);
    void sendListOfArchivedMessagesToPlayer(Player player);
    void archiveMessage(Message message);
    boolean containsUnreadMessages();
    ArrayList<Message> getUnreadMessages();
    void sendListOfUnreadMessagesToPlayer(Player player);
}