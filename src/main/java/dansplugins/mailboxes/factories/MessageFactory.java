package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.managers.ConfigManager;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.utils.UUIDChecker;

import java.util.Random;
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
        int ID = getNewMessageID();
        return new Message(ID, sender, recipient, content);
    }

    public PlayerMessage createPlayerMessage(UUID senderUUID, UUID recipientUUID, String content) {
        int messageID = getNewMessageID();
        String senderName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(senderUUID);
        String recipientName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(recipientUUID);

        return new PlayerMessage(messageID, senderName, recipientName, content, senderUUID, recipientUUID);
    }

    private int getNewMessageID() {
        Random random = new Random();
        int numAttempts = 0;
        int maxAttempts = 25;
        int newID = -1;
        do {
            int maxMessageIDNumber = ConfigManager.getInstance().getInt("maxMessageIDNumber");
            newID = random.nextInt(maxMessageIDNumber);
            numAttempts++;
        } while (isMessageIDTaken(newID) && numAttempts <= maxAttempts);
        return newID;
    }

    private boolean isMessageIDTaken(int messageID) {
        return PersistentData.getInstance().getMessage(messageID) != null;
    }

}
