package dansplugins.mailboxes.objects;

import org.bukkit.entity.Player;

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
    Date getDate();
    void setDate(Date date);
    int getMailboxID();
    void setMailboxID(int ID);
    void sendContentToPlayer(Player player);
    boolean isArchived();
    void setArchived(boolean b);
    boolean isUnread();
    void setUnread(boolean b);
}