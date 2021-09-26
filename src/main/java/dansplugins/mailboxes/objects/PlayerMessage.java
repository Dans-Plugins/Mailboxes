package dansplugins.mailboxes.objects;

import java.util.Map;
import java.util.UUID;

public class PlayerMessage extends Message {

    private UUID senderUUID;
    private UUID recipientUUID;

    public PlayerMessage(int ID, String sender, String recipient, String content) {
        super(ID, sender, recipient, content);

    }

    public PlayerMessage(Map<String, String> data) {
        super(data);
    }
}
