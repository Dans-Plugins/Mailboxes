package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class PluginMessage extends Message {

    private UUID recipientUUID;

    public PluginMessage(int ID, String pluginName, String recipient, String content, UUID recipientUUID) {
        super(ID, "Plugin Message", pluginName, recipient, content);
        this.recipientUUID = recipientUUID;
    }

    public PluginMessage(Map<String, String> data) {
        super(data);
    }

    public UUID getRecipientUUID() {
        return recipientUUID;
    }

    public void setRecipientUUID(UUID recipientUUID) {
        this.recipientUUID = recipientUUID;
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = super.save();
        saveMap.put("recipientUUID", gson.toJson(recipientUUID));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        super.load(data);

        recipientUUID = UUID.fromString(gson.fromJson(data.get("recipientUUID"), String.class));
    }

}
