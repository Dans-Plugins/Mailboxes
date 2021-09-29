package dansplugins.mailboxes.managers;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.objects.Mailbox;
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
        if (ConfigManager.getInstance().getBoolean("assignmentAlertEnabled")) {
            player.sendMessage(ChatColor.AQUA + "You have been assigned a mailbox. Type /m help for help.");
        }
    }

    public void alertPlayerIfTheyHaveActiveMessages(Player player) {
        if (!ConfigManager.getInstance().getBoolean("activeMessagesAlertEnabled")) {
            return;
        }
        Mailbox mailbox = PersistentData.getInstance().getMailbox(player);
        if (mailbox == null) {
            Logger.getInstance().log("ERROR: Mailbox is null.");
            return;
        }
        if (mailbox.getActiveMessages().size() > 0) {
            player.sendMessage(ChatColor.GREEN + "You have active messages in your mailbox. Type /m list to view them."); // TODO: add a config option for this message
        }
    }
}
