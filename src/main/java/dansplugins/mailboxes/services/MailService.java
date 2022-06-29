package dansplugins.mailboxes.services;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MailService {
    private final Mailboxes mailboxes;
    private final Logger logger;
    private final PersistentData.LookupService lookupService;

    public MailService(Mailboxes mailboxes, Logger logger, PersistentData.LookupService lookupService) {
        this.mailboxes = mailboxes;
        this.logger = logger;
        this.lookupService = lookupService;
    }

    public boolean sendWelcomeMessage(Player player) {
        return mailboxes.getAPI().sendPluginMessageToPlayer(
                mailboxes.getName(),
                player,
                "Welcome to the Mailboxes plugin! This message was sent using the plugin's external API."
        );
    }

    public boolean sendMessage(Message message) {
        logger.log("Attempting to send message with ID " + message.getID());
        logger.log("Message Sender: " + message.getSender());
        logger.log("Message Recipient: " + message.getRecipient());

        if (message instanceof PlayerMessage) {
            PlayerMessage playerMessage = (PlayerMessage) message;
            logger.log("Type: Player Message");
            return sendPlayerMessage(playerMessage);
        }
        else if (message instanceof PluginMessage) {
            PluginMessage pluginMessage = (PluginMessage) message;
            logger.log("Type: Plugin Message");
            return sendPluginMessage(pluginMessage);
        }
        else {
            return false;
        }
    }

    private boolean sendPlayerMessage(PlayerMessage playerMessage) {
        logger.log("Sender UUID: " + playerMessage.getSenderUUID());
        logger.log("Recipient UUID: " + playerMessage.getRecipientUUID());

        Mailbox mailbox = lookupService.lookup(playerMessage.getRecipientUUID());

        if (mailbox == null) {
            logger.log("Mailbox was null.");
            return false;
        }

        playerMessage.setMailboxID(mailbox.getID());

        mailbox.addActiveMessage(playerMessage);
        Player player = Bukkit.getPlayer(playerMessage.getRecipientUUID());
        try {
            player.sendMessage(ChatColor.AQUA + "You've received a message. Type /m list to view your messages.");
        } catch(Exception e) {

        }
        return true;
    }

    private boolean sendPluginMessage(PluginMessage pluginMessage) {
        logger.log("Recipient UUID: " + pluginMessage.getRecipientUUID());

        Mailbox mailbox = lookupService.lookup(pluginMessage.getRecipientUUID());

        if (mailbox == null) {
            logger.log("Mailbox was null.");
            return false;
        }

        pluginMessage.setMailboxID(mailbox.getID());

        mailbox.addActiveMessage(pluginMessage);
        Player player = Bukkit.getPlayer(pluginMessage.getRecipientUUID());
        try {
            player.sendMessage(ChatColor.AQUA + "You've received a message. Type /m list to view your messages.");
        } catch(Exception e) {

        }
        return true;
    }

}
