package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashMap;
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
    }
}