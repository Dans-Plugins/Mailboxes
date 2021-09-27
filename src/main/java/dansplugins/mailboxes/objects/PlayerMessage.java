package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMessage extends Message {

    private UUID senderUUID;
    private UUID recipientUUID;

    public PlayerMessage(int ID, String sender, String recipient, String content, UUID senderUUID, UUID recipientUUID) {
        super(ID, sender, recipient, content);
        this.senderUUID = senderUUID;
        this.recipientUUID = recipientUUID;
    }

    public PlayerMessage(Map<String, String> data) {
        super(data);
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public void setSenderUUID(UUID senderUUID) {
        this.senderUUID = senderUUID;
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
        saveMap.put("senderUUID", gson.toJson(senderUUID));
        saveMap.put("recipientUUID", gson.toJson(recipientUUID));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        super.load(data);

        senderUUID = UUID.fromString(gson.fromJson(data.get("senderUUID"), String.class));
        recipientUUID = UUID.fromString(gson.fromJson(data.get("recipientUUID"), String.class));
    }
}
