package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.services.ConfigService;

import dansplugins.mailboxes.utils.Logger;
import org.bukkit.entity.Player;

import java.util.Random;

public class MailboxFactory {
    private final ConfigService configService;
    private final PersistentData persistentData;
    private final Logger logger;

    public MailboxFactory(ConfigService configService, PersistentData persistentData, Logger logger) {
        this.configService = configService;
        this.persistentData = persistentData;
        this.logger = logger;
    }

    public Mailbox createMailbox(Player player) {
        int ID = getNewMailboxID();
        return new Mailbox(logger, ID, player);
    }

    private int getNewMailboxID() {
        Random random = new Random();
        int numAttempts = 0;
        int maxAttempts = 25;
        int newID = -1;
        do {
            int maxMailboxIDNumber = configService.getInt("maxMailboxIDNumber");
            newID = random.nextInt(maxMailboxIDNumber);
            numAttempts++;
        } while (isMailboxIDTaken(newID) && numAttempts <= maxAttempts);
        return newID;
    }

    private boolean isMailboxIDTaken(int mailboxID) {
        return persistentData.getMailbox(mailboxID) != null;
    }
}
