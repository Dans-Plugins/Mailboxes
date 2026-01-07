package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.mailboxes.utils.Logger;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mailbox implements Savable {
    private static final int DEFAULT_PAGE_SIZE = 10;
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
        sendListOfActiveMessagesToPlayer(player, 1, DEFAULT_PAGE_SIZE);
    }

    public void sendListOfActiveMessagesToPlayer(Player player, int page, int pageSize) {
        sendPaginatedMessageList(player, activeMessages, page, pageSize, "Active Messages", "active");
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
        sendListOfArchivedMessagesToPlayer(player, 1, DEFAULT_PAGE_SIZE);
    }

    public void sendListOfArchivedMessagesToPlayer(Player player, int page, int pageSize) {
        sendPaginatedMessageList(player, archivedMessages, page, pageSize, "Archived Messages", "archived");
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
        sendListOfUnreadMessagesToPlayer(player, 1, DEFAULT_PAGE_SIZE);
    }

    public void sendListOfUnreadMessagesToPlayer(Player player, int page, int pageSize) {
        ArrayList<Message> unreadMessages = getUnreadMessages();
        sendPaginatedMessageList(player, unreadMessages, page, pageSize, "Unread Messages", "unread");
    }

    private void sendPaginatedMessageList(Player player, ArrayList<Message> messages, int page, int pageSize, String listTitle, String listType) {
        if (messages.size() == 0) {
            if (page > 1) {
                player.sendMessage(ChatColor.RED + "Invalid page number. You don't have any " + listType + " messages.");
            } else {
                player.sendMessage(ChatColor.AQUA + "You don't have any " + listType + " messages at this time.");
            }
            return;
        }

        int totalPages = (int) Math.ceil((double) messages.size() / pageSize);
        if (page < 1 || page > totalPages) {
            player.sendMessage(ChatColor.RED + "Invalid page number. Valid pages: 1-" + totalPages);
            return;
        }

        player.sendMessage(ChatColor.AQUA + "=== " + listTitle + " (Page " + page + "/" + totalPages + ") ===");
        player.sendMessage(ChatColor.AQUA + "D: date, S: sender, 📎: has attachments");

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, messages.size());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = startIndex; i < endIndex; i++) {
            Message message = messages.get(i);
            
            // Create clickable message ID
            TextComponent idComponent = new TextComponent("ID: " + message.getID());
            idComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
            idComponent.setBold(message.isUnread());
            idComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/m open " + message.getID()));
            idComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    new ComponentBuilder("Click to open message").create()));
            
            // Create non-clickable message info
            String messageInfo = " - D: " + dateFormat.format(message.getDate()) + " - S: " + message.getSender();
            if (message.hasAttachments()) {
                messageInfo += " 📎";
            }
            
            TextComponent infoComponent = new TextComponent(messageInfo);
            infoComponent.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            infoComponent.setBold(message.isUnread());
            
            // Combine components
            ComponentBuilder messageBuilder = new ComponentBuilder("* ")
                    .color(net.md_5.bungee.api.ChatColor.AQUA)
                    .bold(message.isUnread());
            messageBuilder.append(idComponent, ComponentBuilder.FormatRetention.NONE);
            messageBuilder.append(infoComponent, ComponentBuilder.FormatRetention.NONE);
            
            player.spigot().sendMessage(messageBuilder.create());
        }

        // Show navigation info
        if (totalPages > 1) {
            ComponentBuilder navigationBuilder = new ComponentBuilder("Page " + page + " of " + totalPages)
                    .color(net.md_5.bungee.api.ChatColor.GRAY);
            
            if (page > 1) {
                navigationBuilder.append(" | ", ComponentBuilder.FormatRetention.NONE)
                        .color(net.md_5.bungee.api.ChatColor.GRAY);
                TextComponent previousLink = new TextComponent("[Previous Page]");
                previousLink.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                previousLink.setBold(true);
                previousLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/m list " + listType + " " + (page - 1)));
                previousLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder("Click to go to page " + (page - 1)).create()));
                navigationBuilder.append(previousLink, ComponentBuilder.FormatRetention.NONE);
            }
            
            if (page < totalPages) {
                navigationBuilder.append(" | ", ComponentBuilder.FormatRetention.NONE)
                        .color(net.md_5.bungee.api.ChatColor.GRAY);
                TextComponent nextLink = new TextComponent("[Next Page]");
                nextLink.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                nextLink.setBold(true);
                nextLink.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/m list " + listType + " " + (page + 1)));
                nextLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder("Click to go to page " + (page + 1)).create()));
                navigationBuilder.append(nextLink, ComponentBuilder.FormatRetention.NONE);
            }
            
            player.spigot().sendMessage(navigationBuilder.create());
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