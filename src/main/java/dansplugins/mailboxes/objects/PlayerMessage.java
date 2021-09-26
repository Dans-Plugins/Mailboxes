package dansplugins.mailboxes.objects;

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
}
