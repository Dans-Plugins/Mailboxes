package dansplugins.mailboxes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Mailboxes Commands ===");
        sender.sendMessage(ChatColor.AQUA + "/m help");
        sender.sendMessage(ChatColor.AQUA + "/m list");
        sender.sendMessage(ChatColor.AQUA + "/m open");
        sender.sendMessage(ChatColor.AQUA + "/m send");
        sender.sendMessage(ChatColor.AQUA + "/m delete");
        sender.sendMessage(ChatColor.AQUA + "/m config");
        return true;
    }

}
