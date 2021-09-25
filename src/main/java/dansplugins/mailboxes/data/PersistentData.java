package dansplugins.mailboxes.data;

import dansplugins.mailboxes.objects.Mailbox;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PersistentData {

    private static PersistentData instance;

    private ArrayList<Mailbox> mailboxes = new ArrayList<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public ArrayList<Mailbox> getMailboxes() {
        return mailboxes;
    }

    public Mailbox getMailbox(Player player) {
        // TODO: implement
        return null;
    }

    public Mailbox getMailbox(UUID playerUUID) {
        // TODO: implement
        return null;
    }

    public void addMailbox(Mailbox mailbox) {
        if (!mailboxes.contains(mailbox)) {
            mailboxes.add(mailbox);
        }
    }

    public void removeMailbox(Mailbox mailbox) {
        mailboxes.remove(mailbox);
    }

}
