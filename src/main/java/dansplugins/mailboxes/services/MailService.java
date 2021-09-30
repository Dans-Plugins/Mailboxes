package dansplugins.mailboxes.services;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MailService implements IMailService {

    private static MailService instance;

    private MailService() {

    }

    public static MailService getInstance() {
        if (instance == null) {
            instance = new MailService();
        }
        return instance;
    }

    public boolean sendWelcomeMessage(Player player) {
        return Mailboxes.getInstance().getAPI().sendPluginMessageToPlayer(
                Mailboxes.getInstance().getName(),
                player,
                "Welcome to the Mailboxes plugin! This message was sent using the plugin's external API."
        );
    }

    public boolean sendMessage(Message message) {
        Logger.getInstance().log("Attempting to send message with ID " + message.getID());
        Logger.getInstance().log("Message Sender: " + message.getSender());
        Logger.getInstance().log("Message Recipient: " + message.getRecipient());

        if (message instanceof PlayerMessage) {
            PlayerMessage playerMessage = (PlayerMessage) message;
            Logger.getInstance().log("Type: Player Message");
            return sendPlayerMessage(playerMessage);
        }
        else if (message instanceof PluginMessage) {
            PluginMessage pluginMessage = (PluginMessage) message;
            Logger.getInstance().log("Type: Plugin Message");
            return sendPluginMessage(pluginMessage);
        }
        else {
            return false;
        }
    }

    private boolean sendPlayerMessage(PlayerMessage playerMessage) {
        Logger.getInstance().log("Sender UUID: " + playerMessage.getSenderUUID());
        Logger.getInstance().log("Recipient UUID: " + playerMessage.getRecipientUUID());

        Mailbox mailbox = MailboxLookupService.getInstance().lookup(playerMessage.getRecipientUUID());

        if (mailbox == null) {
            Logger.getInstance().log("Mailbox was null.");
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
        Logger.getInstance().log("Recipient UUID: " + pluginMessage.getRecipientUUID());

        Mailbox mailbox = MailboxLookupService.getInstance().lookup(pluginMessage.getRecipientUUID());

        if (mailbox == null) {
            Logger.getInstance().log("Mailbox was null.");
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
