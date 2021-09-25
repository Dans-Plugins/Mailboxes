package dansplugins.mailboxes.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Mailbox implements IMailbox, Savable {

    private UUID ownerUUID;
    private ArrayList<Message> messages = new ArrayList<>();

    public Mailbox(UUID uuid) {
        ownerUUID = uuid;
    }

    public Mailbox(Player player) {
        ownerUUID = player.getUniqueId();
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        ownerUUID = uuid;
    }

    @Override
    public ArrayList<Message> getMessages() {
        return messages;
    }

    @Override
    public Message getMessage(int ID) {
        for (Message message : messages) {
            if (message.getID() == ID) {
                return message;
            }
        }
        return null;
    }

    @Override
    public void addMessage(Message message) {
        if (getMessage(message.getID()) == null) {
            messages.add(message);
        }
    }

    @Override
    public void removeMessage(Message message) {
        messages.remove(message);
    }

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> data) {
        // TODO: implement
    }
}