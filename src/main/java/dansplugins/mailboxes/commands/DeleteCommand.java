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

public class DeleteCommand {
    private final PersistentData persistentData;

    public DeleteCommand(PersistentData persistentData) {
        this.persistentData = persistentData;
    }

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

        Mailbox mailbox = persistentData.getMailbox(player);

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

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2 && sender instanceof Player) {
            Player player = (Player) sender;
            Mailbox mailbox = persistentData.getMailbox(player);
            
            if (mailbox != null) {
                // Use Set to avoid duplicates (though unlikely based on design)
                Set<String> messageIds = new HashSet<>();
                
                // Add active message IDs
                for (Message message : mailbox.getActiveMessages()) {
                    messageIds.add(String.valueOf(message.getID()));
                }
                
                // Add archived message IDs
                for (Message message : mailbox.getArchivedMessages()) {
                    messageIds.add(String.valueOf(message.getID()));
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
