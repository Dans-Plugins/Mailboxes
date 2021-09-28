package dansplugins.mailboxes.externalapi;

import org.bukkit.entity.Player;

import java.util.Date;

public interface IM_Message {
    int getID();
    String getSender();
    String getRecipient();
    String getContent();
    Date getDate();
    int getMailboxID();
    void sendContentToPlayer(Player player);
    boolean isArchived();
}
