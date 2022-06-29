package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mailbox implements Savable {
    private final Logger logger;

    private int ID;
    private UUID ownerUUID;
    private ArrayList<Message> activeMessages = new ArrayList<>();
    private ArrayList<Message> archivedMessages = new ArrayList<>();

    public Mailbox(Logger logger, int ID, UUID uuid) {
        this.logger = logger;
        this.ID = ID;
        this.ownerUUID = uuid;
    }

    public Mailbox(Logger logger, int ID, Player player) {
        this.logger = logger;
        this.ID = ID;
        this.ownerUUID = player.getUniqueId();
    }

    public Mailbox(Map<String, String> data, Logger logger) {
        this.logger = logger;
        this.load(data);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID uuid) {
        ownerUUID = uuid;
    }

    public Message getMessage(int ID) {
        Message message = getActiveMessage(ID);
        if (message != null) {
            return message;
        }
        else {
            return getArchivedMessage(ID);
        }
    }

    public void removeMessage(Message message) {
        removeActiveMessage(message);
        removeArchivedMessage(message);
    }

    public ArrayList<Message> getActiveMessages() {
        return activeMessages;
    }

    public Message getActiveMessage(int ID) {
        for (Message message : activeMessages) {
            if (message.getID() == ID) {
                return message;
            }
        }
        return null;
    }

    public void addActiveMessage(Message message) {
        if (getActiveMessage(message.getID()) == null) {
            activeMessages.add(message);
        }
    }

    public void removeActiveMessage(Message message) {
        activeMessages.remove(message);
    }

    public void removeActiveMessage(int ID) {
        Message message = getActiveMessage(ID);
        removeActiveMessage(message);
    }

    public void sendListOfActiveMessagesToPlayer(Player player) {
        if (activeMessages.size() == 0) {
            player.sendMessage(ChatColor.AQUA + "You don't have any active messages at this time.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "=== Active Messages ===");
        player.sendMessage(ChatColor.AQUA + "D: date, S: sender");
        for (Message message : activeMessages) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String toSend = "* ID: " + message.getID() + " - D: " + dateFormat.format(message.getDate()) + " - S: " + message.getSender();
            if (message.isUnread()) {
                toSend = ChatColor.BOLD + toSend;
            }
            player.sendMessage(ChatColor.AQUA + toSend);
        }
    }

    public ArrayList<Message> getArchivedMessages() {
        return archivedMessages;
    }

    public Message getArchivedMessage(int ID) {
        for (Message message : archivedMessages) {
            if (message.getID() == ID) {
                return message;
            }
        }
        return null;
    }

    public void addArchivedMessage(Message message) {
        if (getArchivedMessage(message.getID()) == null) {
            archivedMessages.add(message);
        }
    }

    public void removeArchivedMessage(Message message) {
        archivedMessages.remove(message);
    }

    public void removeArchivedMessage(int ID) {
        Message message = getArchivedMessage(ID);
        removeArchivedMessage(message);
    }

    public void sendListOfArchivedMessagesToPlayer(Player player) {
        if (archivedMessages.size() == 0) {
            player.sendMessage(ChatColor.AQUA + "You don't have any archived messages at this time.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "=== Archived Messages ===");
        player.sendMessage(ChatColor.AQUA + "D: date, S: sender");
        for (Message message : archivedMessages) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String toSend = "* ID: " + message.getID() + " - D: " + dateFormat.format(message.getDate()) + " - S: " + message.getSender();
            if (message.isUnread()) {
                toSend = ChatColor.BOLD + toSend;
            }
            player.sendMessage(ChatColor.AQUA + toSend);
        }
    }

    public void archiveMessage(Message message) {
        logger.log("Archiving message with ID: " + message.getID());
        message.setArchived(true);
        removeActiveMessage(message);
        addArchivedMessage(message);
    }

    public boolean containsUnreadMessages() {
        for (Message message : activeMessages) {
            if (message.isUnread()) {
                return true;
            }
        }
        for (Message message : archivedMessages) {
            if (message.isUnread()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Message> getUnreadMessages() {
        ArrayList<Message> toReturn = new ArrayList<>();
        for (Message message : activeMessages) {
            if (message.isUnread()) {
                toReturn.add(message);
            }
        }
        for (Message message : archivedMessages) {
            if (message.isUnread()) {
                toReturn.add(message);
            }
        }
        return toReturn;
    }

    public void sendListOfUnreadMessagesToPlayer(Player player) {
        ArrayList<Message> unreadMessages = getUnreadMessages();
        if (unreadMessages.size() == 0) {
            player.sendMessage(ChatColor.AQUA + "You don't have any unread messages at this time.");
            return;
        }
        player.sendMessage(ChatColor.AQUA + "=== Unread Messages ===");
        player.sendMessage(ChatColor.AQUA + "D: date, S: sender");
        for (Message message : getUnreadMessages()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String toSend = "* ID: " + message.getID() + " - D: " + dateFormat.format(message.getDate()) + " - S: " + message.getSender();
            toSend = ChatColor.BOLD + toSend;
            player.sendMessage(ChatColor.AQUA + toSend);
        }
    }

    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("ownerUUID", gson.toJson(ownerUUID));

        return saveMap;
    }

    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        ownerUUID = UUID.fromString(gson.fromJson(data.get("ownerUUID"), String.class));
    }
}