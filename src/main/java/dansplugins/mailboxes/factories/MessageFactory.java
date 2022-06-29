package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;

import java.util.Random;
import java.util.UUID;

public class MessageFactory {
    private final UUIDChecker uuidChecker;
    private final ConfigService configService;
    private final PersistentData persistentData;
    private final Logger logger;

    public MessageFactory(UUIDChecker uuidChecker, ConfigService configService, PersistentData persistentData, Logger logger) {
        this.uuidChecker = uuidChecker;
        this.configService = configService;
        this.persistentData = persistentData;
        this.logger = logger;
    }

    public Message createMessage(String sender, String recipient, String content) {
        int ID = getNewMessageID();
        return new Message(logger, configService, ID, "Default Message", sender, recipient, content);
    }

    public PlayerMessage createPlayerMessage(UUID senderUUID, UUID recipientUUID, String content) {
        int messageID = getNewMessageID();
        String senderName = uuidChecker.findPlayerNameBasedOnUUID(senderUUID);
        String recipientName = uuidChecker.findPlayerNameBasedOnUUID(recipientUUID);
        return new PlayerMessage(messageID, senderName, recipientName, content, senderUUID, recipientUUID, logger, configService);
    }

    public PluginMessage createPluginMessage(String pluginName, UUID recipientUUID, String content) {
        int messageID = getNewMessageID();
        String recipientName = uuidChecker.findPlayerNameBasedOnUUID(recipientUUID);
        return new PluginMessage(messageID, pluginName, recipientName, content, recipientUUID, logger, configService);
    }

    private int getNewMessageID() {
        Random random = new Random();
        int numAttempts = 0;
        int maxAttempts = 25;
        int newID = -1;
        do {
            int maxMessageIDNumber = configService.getInt("maxMessageIDNumber");
            newID = random.nextInt(maxMessageIDNumber);
            numAttempts++;
        } while (isMessageIDTaken(newID) && numAttempts <= maxAttempts);
        return newID;
    }

    private boolean isMessageIDTaken(int messageID) {
        return persistentData.getMessage(messageID) != null;
    }

}
