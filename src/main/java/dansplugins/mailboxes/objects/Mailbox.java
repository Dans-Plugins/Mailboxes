package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mailbox implements IMailbox, Savable {

    private int ID;
    private UUID ownerUUID;
    private ArrayList<Message> activeMessages = new ArrayList<>();

    public Mailbox(int ID, UUID uuid) {
        this.ID = ID;
        this.ownerUUID = uuid;
    }

    public Mailbox(int ID, Player player) {
        this.ID = ID;
        this.ownerUUID = player.getUniqueId();
    }

    public Mailbox(Map<String, String> data) {
        this.load(data);
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int ID) {
        this.ID = ID;
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
    public ArrayList<Message> getActiveMessages() {
        return activeMessages;
    }

    @Override
    public Message getActiveMessage(int ID) {
        for (Message message : activeMessages) {
            if (message.getID() == ID) {
                return message;
            }
        }
        return null;
    }

    @Override
    public void addActiveMessage(Message message) {
        if (getActiveMessage(message.getID()) == null) {
            activeMessages.add(message);
        }
    }

    @Override
    public void removeActiveMessage(Message message) {
        activeMessages.remove(message);
    }

    @Override
    public void removeActiveMessage(int ID) {
        Message message = getActiveMessage(ID);
        removeActiveMessage(message);
    }

    @Override
    public void sendListOfActiveMessagesToPlayer(Player player) {
        if (activeMessages.size() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any messages at this time.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "=== Messages ===");
        for (Message message : activeMessages) {
            player.sendMessage(ChatColor.AQUA + "ID: " + message.getID() + " - Date: " + message.getDate().toString() + " - Sender: " + message.getSender());
        }
    }

    @Override
    public void archiveMessage(Message message) {
        Logger.getInstance().log("Archiving message with ID: " + message.getID());
        message.setArchived(true);
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("ownerUUID", gson.toJson(ownerUUID));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        ownerUUID = UUID.fromString(gson.fromJson(data.get("ownerUUID"), String.class));
    }
}