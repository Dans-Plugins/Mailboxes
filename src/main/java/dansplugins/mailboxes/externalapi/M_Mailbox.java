package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class M_Mailbox implements IM_Mailbox {
    @Override
    public int getID() {
        // TODO: implement
        return 0;
    }

    @Override
    public UUID getOwnerUUID() {
        // TODO: implement
        return null;
    }

    @Override
    public Message getMessage(int ID) {
        // TODO: implement
        return null;
    }

    @Override
    public ArrayList<Message> getActiveMessages() {
        // TODO: implement
        return null;
    }

    @Override
    public Message getActiveMessage(int ID) {
        // TODO: implement
        return null;
    }

    @Override
    public ArrayList<Message> getArchivedMessages() {
        // TODO: implement
        return null;
    }

    @Override
    public Message getArchivedMessage(int ID) {
        // TODO: implement
        return null;
    }

    @Override
    public void sendListOfActiveMessagesToPlayer(Player player) {
        // TODO: implement
    }

    @Override
    public void sendListOfArchivedMessagesToPlayer(Player player) {
        // TODO: implement
    }

    @Override
    public void addArchivedMessage(Message message) {
        // TODO: implement
    }

    @Override
    public void removeArchivedMessage(Message message) {
        // TODO: implement
    }

    @Override
    public void removeArchivedMessage(int ID) {
        // TODO: implement
    }

    @Override
    public void addActiveMessage(Message message) {
        // TODO: implement
    }

    @Override
    public void removeActiveMessage(Message message) {
        // TODO: implement
    }

    @Override
    public void removeActiveMessage(int ID) {
        // TODO: implement
    }

    @Override
    public void removeMessage(Message message) {
        // TODO: implement
    }

    @Override
    public void archiveMessage(Message message) {
        // TODO: implement
    }
}
