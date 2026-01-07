package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArchiveCommand {
    private final PersistentData persistentData;

    public ArchiveCommand(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: add message
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /m archive (ID)");
            return false;
        }

        int ID = Integer.parseInt(args[0]); // TODO: fix error here

        Mailbox mailbox = persistentData.getMailbox(player);

        Message message = mailbox.getMessage(ID);

        if (message == null) {
            player.sendMessage(ChatColor.RED + "That message wasn't found.");
            return false;
        }

        if (message.isArchived()) {
            player.sendMessage(ChatColor.RED + "That message is already archived.");
            return false;
        }

        if (message.getMailboxID() != mailbox.getID()) {
            player.sendMessage(ChatColor.RED + "That message doesn't belong to you.");
            return false;
        }

        mailbox.archiveMessage(message);
        player.sendMessage(ChatColor.GREEN + "Archived.");
        return true;
    }

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2 && sender instanceof Player) {
            Player player = (Player) sender;
            Mailbox mailbox = persistentData.getMailbox(player);
            
            if (mailbox != null) {
                // Use Set to avoid duplicates (though unlikely based on design)
                Set<String> messageIds = new HashSet<>();
                
                // Add active message IDs (only active messages can be archived)
                for (Message message : mailbox.getActiveMessages()) {
                    if (!message.isArchived()) {
                        messageIds.add(String.valueOf(message.getID()));
                    }
                }
                
                return filterCompletions(new ArrayList<>(messageIds), args[1]);
            }
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
