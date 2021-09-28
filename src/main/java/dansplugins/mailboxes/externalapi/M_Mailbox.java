package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class M_Mailbox implements IM_Mailbox {

    private Mailbox mailbox;

    public M_Mailbox(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    // accessors

    @Override
    public int getID() {
        return mailbox.getID();
    }

    @Override
    public UUID getOwnerUUID() {
        return mailbox.getOwnerUUID();
    }

    @Override
    public Message getMessage(int ID) {
        return mailbox.getMessage(ID);
    }

    @Override
    public ArrayList<Message> getActiveMessages() {
        return mailbox.getActiveMessages();
    }

    @Override
    public Message getActiveMessage(int ID) {
        return mailbox.getActiveMessage(ID);
    }

    @Override
    public ArrayList<Message> getArchivedMessages() {
        return mailbox.getArchivedMessages();
    }

    @Override
    public Message getArchivedMessage(int ID) {
        return mailbox.getArchivedMessage(ID);
    }

    @Override
    public void sendListOfActiveMessagesToPlayer(Player player) {
        mailbox.sendListOfActiveMessagesToPlayer(player);
    }

    @Override
    public void sendListOfArchivedMessagesToPlayer(Player player) {
        mailbox.sendListOfArchivedMessagesToPlayer(player);
    }

    // mutators

    @Override
    public void addArchivedMessage(Message message) {
        mailbox.addArchivedMessage(message);
    }

    @Override
    public void removeArchivedMessage(Message message) {
        mailbox.removeArchivedMessage(message);
    }

    @Override
    public void removeArchivedMessage(int ID) {
        mailbox.removeArchivedMessage(ID);
    }

    @Override
    public void addActiveMessage(Message message) {
        mailbox.addActiveMessage(message);
    }

    @Override
    public void removeActiveMessage(Message message) {
        mailbox.removeActiveMessage(message);
    }

    @Override
    public void removeActiveMessage(int ID) {
        mailbox.removeActiveMessage(ID);
    }

    @Override
    public void removeMessage(Message message) {
        mailbox.removeMessage(message);
    }

    @Override
    public void archiveMessage(Message message) {
        mailbox.archiveMessage(message);
    }

}
