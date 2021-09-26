package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        int ID = getNewID();
        return new Mailbox(ID, player);
    }

    private int getNewID() {
        // TODO: implement
        return -1;
    }
}
