package dansplugins.mailboxes.externalapi;

import org.bukkit.entity.Player;

import java.util.Date;

public class M_Message implements IM_Message {
    @Override
    public int getID() {
        // TODO: implement
        return 0;
    }

    @Override
    public String getSender() {
        // TODO: implement
        return null;
    }

    @Override
    public String getRecipient() {
        // TODO: implement
        return null;
    }

    @Override
    public String getContent() {
        // TODO: implement
        return null;
    }

    @Override
    public Date getDate() {
        // TODO: implement
        return null;
    }

    @Override
    public int getMailboxID() {
        // TODO: implement
        return 0;
    }

    @Override
    public void sendContentToPlayer(Player player) {
        // TODO: implement
    }

    @Override
    public boolean isArchived() {
        // TODO: implement
        return false;
    }
}
