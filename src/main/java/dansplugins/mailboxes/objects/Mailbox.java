package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mailbox implements IMailbox, Savable {

    private int ID;
    private UUID ownerUUID;
    private ArrayList<Message> messages = new ArrayList<>();

    public Mailbox(UUID uuid) {
        ownerUUID = uuid;
    }

    public Mailbox(Player player) {
        ownerUUID = player.getUniqueId();
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

    @Override
    public void sendListOfMessagesToPlayer(Player player) {
        if (messages.size() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any messages at this time.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "=== Messages ===");
        for (Message message : messages) {
            player.sendMessage(ChatColor.AQUA + "" + message.getID() + " - " + message.getDate().toString() + " - " + message.getSender());
        }
    }
}