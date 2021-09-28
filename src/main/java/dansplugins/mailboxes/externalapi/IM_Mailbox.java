package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public interface IM_Mailbox {
    // accessors
    int getID();
    UUID getOwnerUUID();
    Message getMessage(int ID);
    ArrayList<Message> getActiveMessages();
    Message getActiveMessage(int ID);
    ArrayList<Message> getArchivedMessages();
    Message getArchivedMessage(int ID);
    void sendListOfActiveMessagesToPlayer(Player player);
    void sendListOfArchivedMessagesToPlayer(Player player);

    // mutators
    void addArchivedMessage(Message message);
    void removeArchivedMessage(Message message);
    void removeArchivedMessage(int ID);
    void addActiveMessage(Message message);
    void removeActiveMessage(Message message);
    void removeActiveMessage(int ID);
    void removeMessage(Message message);
    void archiveMessage(Message message);
}
