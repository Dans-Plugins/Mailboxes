package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand {
    private final Logger logger;
    private final PersistentData persistentData;

    public StatsCommand(Logger logger, PersistentData persistentData) {
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            logger.log("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        Mailbox mailbox = persistentData.getMailbox(player);

        if (mailbox == null) {
            player.sendMessage(ChatColor.RED + "ERROR: Mailbox was not found.");
            return false;
        }

        int activeCount = mailbox.getActiveMessages().size();
        int archivedCount = mailbox.getArchivedMessages().size();
        int unreadCount = mailbox.getUnreadMessages().size();
        int totalCount = activeCount + archivedCount;

        player.sendMessage(ChatColor.AQUA + "=== Mailbox Stats ===");
        player.sendMessage(ChatColor.AQUA + "Total messages: " + totalCount);
        player.sendMessage(ChatColor.AQUA + "Active messages: " + activeCount);
        player.sendMessage(ChatColor.AQUA + "Archived messages: " + archivedCount);
        player.sendMessage(ChatColor.AQUA + "Unread messages: " + unreadCount);

        return true;
    }

}
