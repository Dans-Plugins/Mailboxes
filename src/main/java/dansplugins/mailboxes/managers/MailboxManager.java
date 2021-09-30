package dansplugins.mailboxes.managers;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.services.MailService;
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
        if (ConfigManager.getInstance().getBoolean("welcomeMessageEnabled")) {
            MailService.getInstance().sendWelcomeMessage(player);
        }
    }

    public void alertPlayerIfTheyHaveUnreadMessages(Player player) {
        if (!ConfigManager.getInstance().getBoolean("unreadMessagesAlertEnabled")) {
            return;
        }
        Mailbox mailbox = PersistentData.getInstance().getMailbox(player);
        if (mailbox == null) {
            Logger.getInstance().log("ERROR: Mailbox is null.");
            return;
        }
        if (mailbox.containsUnreadMessages()) {
            player.sendMessage(ChatColor.GREEN + "You have unread messages in your mailbox. Type /m list unread to view them."); // TODO: add a config option for this message
        }
    }
}
