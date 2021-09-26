package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MailService {

    private static MailService instance;

    private MailService() {

    }

    public static MailService getInstance() {
        if (instance == null) {
            instance = new MailService();
        }
        return instance;
    }

    public boolean sendMessage(Message message) {
        Logger.getInstance().log("Attempting to send message with ID " + message.getID());
        Logger.getInstance().log("Message Sender: " + message.getSender());
        Logger.getInstance().log("Message Recipient: " + message.getRecipient());

        if (message instanceof PlayerMessage) {
            PlayerMessage playerMessage = (PlayerMessage) message;
            Mailbox mailbox = PersistentData.getInstance().getMailbox(playerMessage.getRecipientUUID());

            if (mailbox == null) {
                // TODO: add message
                return false;
            }

            mailbox.addMessage(playerMessage);
            Player player = Bukkit.getPlayer(playerMessage.getRecipientUUID());
            try {
                player.sendMessage(ChatColor.AQUA + "You've received a message. Type /m list to view your messages.");
            } catch(Exception e) {

            }
            return true;
        }
        else {
            return false;
        }
    }

}
