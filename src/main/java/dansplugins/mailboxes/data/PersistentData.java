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
        return getMailbox(player.getUniqueId());
    }

    public Mailbox getMailbox(UUID playerUUID) {
        for (Mailbox mailbox : mailboxes) {
            if (mailbox.getOwnerUUID().equals(playerUUID)) {
                return mailbox;
            }
        }
        return null;
    }

    public void addMailbox(Mailbox mailbox) {
        if (getMailbox(mailbox.getOwnerUUID()) == null) {
            mailboxes.add(mailbox);
        }
    }

    public void removeMailbox(Mailbox mailbox) {
        mailboxes.remove(mailbox);
    }

}
