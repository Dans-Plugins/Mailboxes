package dansplugins.mailboxes.managers;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MailboxManager {

    private static MailboxManager instance;

    private MailboxManager() {

    }

    public static MailboxManager getInstance() {
        if (instance == null) {
            instance = new MailboxManager();
        }
        return instance;
    }

    public void assignMailboxToPlayerIfNecessary(Player player ) {
        if (PersistentData.getInstance().getMailbox(player.getUniqueId()) != null) {
            // player already has a mailbox
            return;
        }

        // player doesn't have a mailbox
        Mailbox newMailbox = MailboxFactory.getInstance().createMailbox(player);
        PersistentData.getInstance().addMailbox(newMailbox);
        player.sendMessage(ChatColor.AQUA + "You have been assigned a mailbox. Type /m help for help."); // TODO: add config option for this message
    }

}
