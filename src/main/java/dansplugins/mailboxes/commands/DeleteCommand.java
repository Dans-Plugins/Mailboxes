package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: add message
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /m delete (ID)");
            return false;
        }

        int ID = Integer.parseInt(args[0]); // TODO: fix error here

        Mailbox mailbox = PersistentData.getInstance().getMailbox(player);

        Message message = mailbox.getMessage(ID);

        if (message == null) {
            player.sendMessage(ChatColor.RED + "That message wasn't found.");
            return false;
        }

        if (message.getMailboxID() != mailbox.getID()) {
            player.sendMessage(ChatColor.RED + "That message doesn't belong to you.");
            return false;
        }

        mailbox.removeMessage(message);
        player.sendMessage(ChatColor.GREEN + "Deleted.");
        return true;
    }

}
