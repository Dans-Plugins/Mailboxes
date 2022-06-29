package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand {
    private final Logger logger;
    private final PersistentData persistentData;

    public ListCommand(Logger logger, PersistentData persistentData) {
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            logger.log("Only players can use this command");
            return false;
        }

        Player player = (Player) sender;

        Mailbox mailbox = persistentData.getMailbox(player);

        if (mailbox == null) {
            player.sendMessage(ChatColor.RED + "ERROR: Mailbox was not found.");
            return false;
        }

        if (args.length > 0) {
            String list = args[0];
            if (list.equalsIgnoreCase("active")) {
                mailbox.sendListOfActiveMessagesToPlayer(player);
            }
            else if (list.equalsIgnoreCase("archived")) {
                mailbox.sendListOfArchivedMessagesToPlayer(player);
            }
            else if (list.equalsIgnoreCase("unread")) {
                mailbox.sendListOfUnreadMessagesToPlayer(player);
            }
            else {
                player.sendMessage(ChatColor.RED + "Sub-commands: active, archived, unread");
                return false;
            }
            return true;
        }

        mailbox.sendListOfActiveMessagesToPlayer(player);
        return true;
    }

}
