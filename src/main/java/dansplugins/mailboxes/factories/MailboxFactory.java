package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.managers.ConfigManager;
import dansplugins.mailboxes.objects.Mailbox;
import org.bukkit.entity.Player;

import java.util.Random;

public class MailboxFactory {

    private static MailboxFactory instance;

    private MailboxFactory() {

    }

    public static MailboxFactory getInstance() {
        if (instance == null) {
            instance = new MailboxFactory();
        }
        return instance;
    }


    public Mailbox createMailbox(Player player) {
        int ID = getNewMailboxID();
        return new Mailbox(ID, player);
    }

    private int getNewMailboxID() {
        Random random = new Random();
        int numAttempts = 0;
        int maxAttempts = 25;
        int newID = -1;
        do {
            int maxMailboxIDNumber = ConfigManager.getInstance().getInt("maxMailboxIDNumber");
            newID = random.nextInt(maxMailboxIDNumber);
            numAttempts++;
        } while (isMailboxIDTaken(newID) && numAttempts <= maxAttempts);
        return newID;
    }

    private boolean isMailboxIDTaken(int mailboxID) {
        return PersistentData.getInstance().getMailbox(mailboxID) != null;
    }
}
