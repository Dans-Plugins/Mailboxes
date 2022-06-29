package dansplugins.mailboxes.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PermissionChecker {

    public boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "In order to use this command, you need the following permission: '" + permission + "'");
            return false;
        }
        return true;
    }

}
