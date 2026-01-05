package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements Savable {
    private final Logger logger;
    private final ConfigService configService;

    protected int ID;
    protected String type;
    protected String sender;
    protected String recipient;
    protected String content;
    protected Date date;
    protected int mailboxID;
    protected boolean archived = false;
    protected boolean unread = true;
    protected List<ItemStack> attachments = new ArrayList<>();

    public Message(Logger logger, ConfigService configService, int ID, String type, String sender, String recipient, String content) {
        this.logger = logger;
        this.configService = configService;
        this.ID = ID;
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = new Date();
    }

    public Message(Logger logger, ConfigService configService, int ID, String type, String sender, String recipient, String content, int mailboxID) {
        this.logger = logger;
        this.configService = configService;
        this.ID = ID;
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.date = new Date();
        this.mailboxID = mailboxID;
    }

    public Message(Map<String, String> data, Logger logger, ConfigService configService) {
        this.logger = logger;
        this.configService = configService;
        this.load(data);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMailboxID() {
        return mailboxID;
    }

    public void setMailboxID(int ID) {
        this.mailboxID = ID;
    }

    public void sendContentToPlayer(Player player) {
        logger.log("Message ID: " + ID);
        logger.log("Mailbox ID: " + mailboxID);
        player.sendMessage(ChatColor.AQUA + "=============================");
        player.sendMessage(ChatColor.AQUA + "Type: " + type);
        player.sendMessage(ChatColor.AQUA + "Date: " + date.toString());
        player.sendMessage(ChatColor.AQUA + "From: " + sender);
        player.sendMessage("\n");
        if (configService.getBoolean("quotesEnabled")) {
            player.sendMessage(ChatColor.AQUA + "\"" + content + "\"");
        }
        else {
            player.sendMessage(ChatColor.AQUA + content);
        }

        if (hasAttachments()) {
            player.sendMessage("\n");
            player.sendMessage(ChatColor.GOLD + "Attachments (" + attachments.size() + "):");
            for (ItemStack item : attachments) {
                if (item != null) {
                    String itemName = item.getType().toString().toLowerCase().replace("_", " ");
                    player.sendMessage(ChatColor.YELLOW + "  - " + item.getAmount() + "x " + itemName);
                }
            }
        }

        player.sendMessage(ChatColor.AQUA + "=============================");
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean b) {
        archived = b;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean b) {
        unread = b;
    }

    public List<ItemStack> getAttachments() {
        return attachments == null ? null : new ArrayList<>(attachments);
    }

    public void setAttachments(List<ItemStack> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(ItemStack item) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(item);
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("type", gson.toJson(type));
        saveMap.put("sender", gson.toJson(sender));
        saveMap.put("recipient", gson.toJson(recipient));
        saveMap.put("content", gson.toJson(content));
        saveMap.put("date", gson.toJson(date));
        saveMap.put("mailboxID", gson.toJson(mailboxID));
        saveMap.put("archived", gson.toJson(archived));
        saveMap.put("unread", gson.toJson(unread));
        
        // Serialize attachments
        if (attachments != null && !attachments.isEmpty()) {
            List<Map<String, Object>> serializedAttachments = new ArrayList<>();
            for (ItemStack item : attachments) {
                if (item != null) {
                    serializedAttachments.add(item.serialize());
                }
            }
            saveMap.put("attachments", gson.toJson(serializedAttachments));
        }

        return saveMap;
    }

    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        type = gson.fromJson(data.get("type"), String.class);
        sender = gson.fromJson(data.get("sender"), String.class);
        recipient = gson.fromJson(data.get("recipient"), String.class);
        content = gson.fromJson(data.get("content"), String.class);
        date = gson.fromJson(data.get("date"), Date.class);
        mailboxID = Integer.parseInt(gson.fromJson(data.get("mailboxID"), String.class));
        archived = Boolean.parseBoolean(gson.fromJson(data.get("archived"), String.class));
        unread = Boolean.parseBoolean(gson.fromJson(data.get("unread"), String.class));
        
        // Deserialize attachments
        if (data.containsKey("attachments")) {
            try {
                Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> serializedAttachments = gson.fromJson(data.get("attachments"), listType);
                if (serializedAttachments != null) {
                    attachments = new ArrayList<>();
                    for (Map<String, Object> serializedItem : serializedAttachments) {
                        // Convert numeric values to proper types for ItemStack deserialization
                        Map<String, Object> convertedMap = new HashMap<>();
                        for (Map.Entry<String, Object> entry : serializedItem.entrySet()) {
                            Object value = entry.getValue();
                            // Gson may deserialize numbers as Double, but ItemStack expects Integer
                            if (value instanceof Double) {
                                convertedMap.put(entry.getKey(), ((Double) value).intValue());
                            } else {
                                convertedMap.put(entry.getKey(), value);
                            }
                        }
                        ItemStack item = ItemStack.deserialize(convertedMap);
                        attachments.add(item);
                    }
                }
            } catch (Exception e) {
                logger.log("Error loading attachments: " + e.getMessage());
                attachments = new ArrayList<>();
            }
        }
    }
}