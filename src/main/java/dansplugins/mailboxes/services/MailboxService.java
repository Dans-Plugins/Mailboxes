package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MailboxService {
    private final PersistentData persistentData;
    private final MailboxFactory mailboxFactory;
    private final ConfigService configService;
    private final MailService mailService;
    private final Logger logger;

    public MailboxService(PersistentData persistentData, MailboxFactory mailboxFactory, ConfigService configService, MailService mailService, Logger logger) {
        this.persistentData = persistentData;
        this.mailboxFactory = mailboxFactory;
        this.configService = configService;
        this.mailService = mailService;
        this.logger = logger;
    }

    public void assignMailboxToPlayerIfNecessary(Player player ) {
        if (persistentData.getMailbox(player.getUniqueId()) != null) {
            // player already has a mailbox
            return;
        }

        // player doesn't have a mailbox
        Mailbox newMailbox = mailboxFactory.createMailbox(player);
        persistentData.addMailbox(newMailbox);
        if (configService.getBoolean("assignmentAlertEnabled")) {
            player.sendMessage(ChatColor.AQUA + "You have been assigned a mailbox. Type /m help for help.");
        }
        if (configService.getBoolean("welcomeMessageEnabled")) {
            mailService.sendWelcomeMessage(player);
        }
    }

    public void alertPlayerIfTheyHaveUnreadMessages(Player player) {
        if (!configService.getBoolean("unreadMessagesAlertEnabled")) {
            return;
        }
        Mailbox mailbox = persistentData.getMailbox(player);
        if (mailbox == null) {
            logger.log("ERROR: Mailbox is null.");
            return;
        }
        if (mailbox.containsUnreadMessages()) {
            int count = mailbox.getUnreadMessages().size();
            player.sendMessage(ChatColor.GREEN + "You have " + count + " unread messages in your mailbox. Type /m list unread to view them."); // TODO: add a config option for this message
        }
    }
}
