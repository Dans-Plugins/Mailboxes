package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;

import java.util.Date;

public class M_Message {

    private Message message;

    public M_Message(Message message) {
        this.message = message;
    }

    public int getID() {
        return message.getID();
    }

    public String getSender() {
        return message.getSender();
    }

    public String getRecipient() {
        return message.getRecipient();
    }

    public String getContent() {
        return message.getRecipient();
    }

    public Date getDate() {
        return message.getDate();
    }

    public int getMailboxID() {
        return message.getMailboxID();
    }

    public void sendContentToPlayer(Player player) {
        message.sendContentToPlayer(player);
    }

    public boolean isArchived() {
        return message.isArchived();
    }

}
