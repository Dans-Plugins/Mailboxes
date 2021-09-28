package dansplugins.mailboxes.data;

import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
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

    public Mailbox getMailbox(int mailboxID) {
        for (Mailbox mailbox : mailboxes) {
            if (mailbox.getID() == mailboxID) {
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

    public Message getMessage(int messageID) {
        for (Mailbox mailbox : mailboxes) {
            for (Message message : mailbox.getActiveMessages()) {
                if (message.getID() == messageID) {
                    return message;
                }
            }
        }
        return null;
    }
}
