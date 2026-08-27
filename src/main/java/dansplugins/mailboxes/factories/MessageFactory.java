package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MessageFactory {
    private static final int MAX_RANDOM_ATTEMPTS = 25;

    /**
     * Returned by {@link #getNewMessageID()} when every ID in the configured range is already in use.
     */
    private static final int NO_FREE_MESSAGE_ID = -1;

    private final UUIDChecker uuidChecker;
    private final ConfigService configService;
    private final PersistentData persistentData;
    private final Logger logger;
    private final Random random;

    public MessageFactory(UUIDChecker uuidChecker, ConfigService configService, PersistentData persistentData, Logger logger) {
        this(uuidChecker, configService, persistentData, logger, new Random());
    }

    public MessageFactory(UUIDChecker uuidChecker, ConfigService configService, PersistentData persistentData, Logger logger, Random random) {
        this.uuidChecker = uuidChecker;
        this.configService = configService;
        this.persistentData = persistentData;
        this.logger = logger;
        this.random = random;
    }

    /**
     * @return the new message, or null if no free message ID is available
     */
    public Message createMessage(String sender, String recipient, String content) {
        int ID = getNewMessageID();
        if (ID == NO_FREE_MESSAGE_ID) {
            return null;
        }
        return new Message(logger, configService, ID, "Default Message", sender, recipient, content);
    }

    /**
     * @return the new message, or null if no free message ID is available
     */
    public PlayerMessage createPlayerMessage(UUID senderUUID, UUID recipientUUID, String content) {
        int messageID = getNewMessageID();
        if (messageID == NO_FREE_MESSAGE_ID) {
            return null;
        }
        String senderName = uuidChecker.findPlayerNameBasedOnUUID(senderUUID);
        String recipientName = uuidChecker.findPlayerNameBasedOnUUID(recipientUUID);
        return new PlayerMessage(messageID, senderName, recipientName, content, senderUUID, recipientUUID, logger, configService);
    }

    /**
     * @return the new message, or null if no free message ID is available
     */
    public PluginMessage createPluginMessage(String pluginName, UUID recipientUUID, String content) {
        int messageID = getNewMessageID();
        if (messageID == NO_FREE_MESSAGE_ID) {
            return null;
        }
        String recipientName = uuidChecker.findPlayerNameBasedOnUUID(recipientUUID);
        return new PluginMessage(messageID, pluginName, recipientName, content, recipientUUID, logger, configService);
    }

    /**
     * Allocates an unused message ID.
     *
     * Random draws are tried first so that IDs stay spread across the configured range. Because a
     * draw can keep landing on taken IDs, the range is then swept for the lowest free ID rather
     * than handing back the last (taken) candidate.
     *
     * @return a free message ID, or {@link #NO_FREE_MESSAGE_ID} if every ID in the range is taken
     */
    private int getNewMessageID() {
        int maxMessageIDNumber = configService.getInt("maxMessageIDNumber");
        for (int attempt = 0; attempt < MAX_RANDOM_ATTEMPTS; attempt++) {
            int candidate = random.nextInt(maxMessageIDNumber);
            if (!isMessageIDTaken(candidate)) {
                return candidate;
            }
        }
        return findLowestFreeMessageID(maxMessageIDNumber);
    }

    private int findLowestFreeMessageID(int maxMessageIDNumber) {
        Set<Integer> takenIDs = persistentData.getMessageIDsInUse();
        for (int candidate = 0; candidate < maxMessageIDNumber; candidate++) {
            if (!takenIDs.contains(candidate)) {
                return candidate;
            }
        }
        logger.logError("Every message ID between 0 and " + (maxMessageIDNumber - 1) + " is in use. "
                + "No message can be created until messages are deleted or maxMessageIDNumber is raised.");
        return NO_FREE_MESSAGE_ID;
    }

    private boolean isMessageIDTaken(int messageID) {
        return persistentData.isMessageIDInUse(messageID);
    }

}
