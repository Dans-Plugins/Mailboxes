package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.services.ConfigService;

import dansplugins.mailboxes.utils.Logger;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MailboxFactory {
    private static final int MAX_RANDOM_ATTEMPTS = 25;

    /**
     * Returned by {@link #getNewMailboxID()} when every ID in the configured range is already in use.
     */
    private static final int NO_FREE_MAILBOX_ID = -1;

    private final ConfigService configService;
    private final PersistentData persistentData;
    private final Logger logger;
    private final Random random;

    public MailboxFactory(ConfigService configService, PersistentData persistentData, Logger logger) {
        this(configService, persistentData, logger, new Random());
    }

    public MailboxFactory(ConfigService configService, PersistentData persistentData, Logger logger, Random random) {
        this.configService = configService;
        this.persistentData = persistentData;
        this.logger = logger;
        this.random = random;
    }

    /**
     * @return the new mailbox, or null if no free mailbox ID is available
     */
    public Mailbox createMailbox(Player player) {
        int ID = getNewMailboxID();
        if (ID == NO_FREE_MAILBOX_ID) {
            return null;
        }
        return new Mailbox(logger, ID, player);
    }

    /**
     * Allocates an unused mailbox ID.
     *
     * Random draws are tried first so that IDs stay spread across the configured range. Because a
     * draw can keep landing on taken IDs, the range is then swept for the lowest free ID rather
     * than handing back the last (taken) candidate.
     *
     * @return a free mailbox ID, or {@link #NO_FREE_MAILBOX_ID} if every ID in the range is taken
     */
    private int getNewMailboxID() {
        int maxMailboxIDNumber = configService.getInt("maxMailboxIDNumber");
        for (int attempt = 0; attempt < MAX_RANDOM_ATTEMPTS; attempt++) {
            int candidate = random.nextInt(maxMailboxIDNumber);
            if (!isMailboxIDTaken(candidate)) {
                return candidate;
            }
        }
        return findLowestFreeMailboxID(maxMailboxIDNumber);
    }

    private int findLowestFreeMailboxID(int maxMailboxIDNumber) {
        Set<Integer> takenIDs = new HashSet<>();
        for (Mailbox mailbox : persistentData.getMailboxes()) {
            takenIDs.add(mailbox.getID());
        }
        for (int candidate = 0; candidate < maxMailboxIDNumber; candidate++) {
            if (!takenIDs.contains(candidate)) {
                return candidate;
            }
        }
        logger.logError("Every mailbox ID between 0 and " + (maxMailboxIDNumber - 1) + " is in use. "
                + "No mailbox can be created until mailboxes are removed or maxMailboxIDNumber is raised.");
        return NO_FREE_MAILBOX_ID;
    }

    private boolean isMailboxIDTaken(int mailboxID) {
        return persistentData.getMailbox(mailboxID) != null;
    }
}
