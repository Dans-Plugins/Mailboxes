package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;

import java.util.Date;

public class M_Message implements IM_Message {

    private Message message;

    public M_Message(Message message) {
        this.message = message;
    }

    @Override
    public int getID() {
        return message.getID();
    }

    @Override
    public String getSender() {
        return message.getSender();
    }

    @Override
    public String getRecipient() {
        return message.getRecipient();
    }

    @Override
    public String getContent() {
        return message.getRecipient();
    }

    @Override
    public Date getDate() {
        return message.getDate();
    }

    @Override
    public int getMailboxID() {
        return message.getMailboxID();
    }

    @Override
    public void sendContentToPlayer(Player player) {
        message.sendContentToPlayer(player);
    }

    @Override
    public boolean isArchived() {
        return message.isArchived();
    }

}
