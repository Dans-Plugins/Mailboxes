package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        int page = 1;
        int pageSize = 10;
        String listType = "active";

        if (args.length > 0) {
            listType = args[0].toLowerCase();
            
            // Parse page number if provided
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page < 1) {
                        player.sendMessage(ChatColor.RED + "Page number must be 1 or greater.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid page number: " + args[1]);
                    return false;
                }
            }

            if (listType.equals("active")) {
                mailbox.sendListOfActiveMessagesToPlayer(player, page, pageSize);
            }
            else if (listType.equals("archived")) {
                mailbox.sendListOfArchivedMessagesToPlayer(player, page, pageSize);
            }
            else if (listType.equals("unread")) {
                mailbox.sendListOfUnreadMessagesToPlayer(player, page, pageSize);
            }
            else {
                player.sendMessage(ChatColor.RED + "Sub-commands: active, archived, unread");
                player.sendMessage(ChatColor.AQUA + "Usage: /m list [type] [page]");
                return false;
            }
            return true;
        }

        mailbox.sendListOfActiveMessagesToPlayer(player, page, pageSize);
        return true;
    }

    public List<String> getTabCompletions(String[] args) {
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("active", "archived", "unread"), args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filterCompletions(List<String> options, String input) {
        String lowerInput = input.toLowerCase();
        return options.stream()
            .filter(option -> option.toLowerCase().startsWith(lowerInput))
            .collect(Collectors.toList());
    }

}
