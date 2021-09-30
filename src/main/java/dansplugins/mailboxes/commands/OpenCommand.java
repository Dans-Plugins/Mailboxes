package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Logger.getInstance().log("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /m open (ID)");
            return false;
        }

        int ID = Integer.parseInt(args[0]); // TODO: handle error

        Mailbox mailbox = PersistentData.getInstance().getMailbox(player);

        if (mailbox == null) {
            player.sendMessage(ChatColor.RED + "Error: Mailbox wasn't found.");
            return false;
        }

        Message message = mailbox.getMessage(ID);

        if (message == null) {
            player.sendMessage(ChatColor.RED + "A message with that ID wasn't found.");
            return false;
        }

        message.sendContentToPlayer(player);
        message.setUnread(false);
        return true;
    }

}
