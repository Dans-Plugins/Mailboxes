package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageFactory {

    private static MessageFactory instance;

    private MessageFactory() {

    }

    public static MessageFactory getInstance() {
        if (instance == null) {
            instance = new MessageFactory();
        }
        return instance;
    }

    public Message createMessage(String sender, String recipient, String content) {
        int ID = getNewID();
        return new Message(ID, sender, recipient, content);
    }

    public Message createPlayerMessage(UUID senderUUID, UUID recipientUUID, String content) {
        int ID = getNewID();
        String senderName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(senderUUID);
        String recipientName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(recipientUUID);

        return new PlayerMessage(ID, senderName, recipientName, content);
    }

    private int getNewID() {
        // TODO: implement
        return -1;
    }

}
