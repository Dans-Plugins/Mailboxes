package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PluginMessage extends Message {

    private String pluginName;
    private UUID recipientUUID;

    public PluginMessage(int ID, String sender, String recipient, String content, String pluginName, UUID recipientUUID) {
        super(ID, sender, recipient, content);
        this.pluginName = pluginName;
        this.recipientUUID = recipientUUID;
    }

    public PluginMessage(Map<String, String> data) {
        super(data);
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public UUID getRecipientUUID() {
        return recipientUUID;
    }

    public void setRecipientUUID(UUID recipientUUID) {
        this.recipientUUID = recipientUUID;
    }

    @Override
    public void sendContentToPlayer(Player player) {
        Logger.getInstance().log("Message ID: " + ID);
        Logger.getInstance().log("Mailbox ID: " + mailboxID);
        player.sendMessage(ChatColor.AQUA + "=============================");
        player.sendMessage(ChatColor.AQUA + "Type: Plugin Message");
        player.sendMessage(ChatColor.AQUA + "Plugin: " + pluginName);
        player.sendMessage(ChatColor.AQUA + "Date: " + date.toString());
        player.sendMessage(ChatColor.AQUA + "From: " + sender);
        player.sendMessage(ChatColor.AQUA + "\"" + content + "\"");
        player.sendMessage(ChatColor.AQUA + "=============================");
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = super.save();
        saveMap.put("pluginName", gson.toJson(pluginName));
        saveMap.put("recipientUUID", gson.toJson(recipientUUID));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        super.load(data);

        pluginName = gson.fromJson(data.get("pluginName"), String.class);
        recipientUUID = UUID.fromString(gson.fromJson(data.get("recipientUUID"), String.class));
    }

}
