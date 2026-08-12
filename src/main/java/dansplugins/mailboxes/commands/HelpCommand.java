package dansplugins.mailboxes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Mailboxes Commands ===");
        sender.sendMessage(ChatColor.AQUA + "/m help - View a list of helpful commands.");
        sender.sendMessage(ChatColor.AQUA + "/m list [type] [page] - List your messages with pagination.");
        sender.sendMessage(ChatColor.AQUA + "  type is one of active, archived or unread. Defaults to active.");
        sender.sendMessage(ChatColor.AQUA + "/m open (ID) - Open a message.");
        sender.sendMessage(ChatColor.AQUA + "/m send (player) \"message\" [-attach] - Send a message to another player.");
        sender.sendMessage(ChatColor.AQUA + "  Add -attach flag to attach the item in your hand.");
        sender.sendMessage(ChatColor.AQUA + "/m delete (ID) - Delete a message.");
        sender.sendMessage(ChatColor.AQUA + "/m archive (ID) - Archive a message.");
        sender.sendMessage(ChatColor.AQUA + "/m config - View or set config options.");
        sender.sendMessage(ChatColor.AQUA + "/m stats - View your mailbox statistics.");
        return true;
    }

}
