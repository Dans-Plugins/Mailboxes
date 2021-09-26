package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            // TODO: add message
            return false;
        }

        Player player = (Player) sender;

        Mailbox mailbox = PersistentData.getInstance().getMailbox(player);

        if (mailbox == null) {
            player.sendMessage(ChatColor.RED + "ERROR: Mailbox was not found.");
            return false;
        }

        mailbox.sendListOfMessagesToPlayer(player);
        return true;
    }

}
