package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class M_Mailbox {
    private Mailbox mailbox;

    public M_Mailbox(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    // accessors

    public int getID() {
        return mailbox.getID();
    }

    public UUID getOwnerUUID() {
        return mailbox.getOwnerUUID();
    }

    public Message getMessage(int ID) {
        return mailbox.getMessage(ID);
    }

    public ArrayList<Message> getActiveMessages() {
        return mailbox.getActiveMessages();
    }

    public Message getActiveMessage(int ID) {
        return mailbox.getActiveMessage(ID);
    }

    public ArrayList<Message> getArchivedMessages() {
        return mailbox.getArchivedMessages();
    }

    public Message getArchivedMessage(int ID) {
        return mailbox.getArchivedMessage(ID);
    }

    public void sendListOfActiveMessagesToPlayer(Player player) {
        mailbox.sendListOfActiveMessagesToPlayer(player);
    }

    public void sendListOfArchivedMessagesToPlayer(Player player) {
        mailbox.sendListOfArchivedMessagesToPlayer(player);
    }

    // mutators

    public void addArchivedMessage(Message message) {
        mailbox.addArchivedMessage(message);
    }

    public void removeArchivedMessage(Message message) {
        mailbox.removeArchivedMessage(message);
    }

    public void removeArchivedMessage(int ID) {
        mailbox.removeArchivedMessage(ID);
    }

    public void addActiveMessage(Message message) {
        mailbox.addActiveMessage(message);
    }

    public void removeActiveMessage(Message message) {
        mailbox.removeActiveMessage(message);
    }

    public void removeActiveMessage(int ID) {
        mailbox.removeActiveMessage(ID);
    }

    public void removeMessage(Message message) {
        mailbox.removeMessage(message);
    }

    public void archiveMessage(Message message) {
        mailbox.archiveMessage(message);
    }

}
