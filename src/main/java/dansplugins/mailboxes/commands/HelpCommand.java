package dansplugins.mailboxes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Mailboxes Commands ===");
        sender.sendMessage(ChatColor.AQUA + "/m help - View a list of helpful commands.");
        sender.sendMessage(ChatColor.AQUA + "/m list [type] [page] - List your messages with pagination.");
        sender.sendMessage(ChatColor.AQUA + "/m open - Open a message.");
        sender.sendMessage(ChatColor.AQUA + "/m send - Send a message to another player.");
        sender.sendMessage(ChatColor.AQUA + "/m delete - Delete a message.");
        sender.sendMessage(ChatColor.AQUA + "/m archive - Archive a message.");
        sender.sendMessage(ChatColor.AQUA + "/m config - View or set config options.");
        return true;
    }

}
