package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenCommand {
    private final Logger logger;
    private final PersistentData persistentData;

    public OpenCommand(Logger logger, PersistentData persistentData) {
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            logger.log("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /m open (ID)");
            return false;
        }

        int ID = Integer.parseInt(args[0]); // TODO: handle error

        Mailbox mailbox = persistentData.getMailbox(player);

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

        // Deliver attachments to player
        if (message.hasAttachments()) {
            List<ItemStack> attachments = message.getAttachments();
            HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(attachments.toArray(new ItemStack[0]));
            
            if (failedItems.isEmpty()) {
                player.sendMessage(ChatColor.GREEN + "All attached items have been added to your inventory.");
                message.setAttachments(new ArrayList<>()); // Clear attachments after successful delivery
            } else {
                player.sendMessage(ChatColor.YELLOW + "Some items couldn't fit in your inventory and remain attached to the message.");
                player.sendMessage(ChatColor.YELLOW + "Please free up space and open the message again.");
                // Keep failed items as attachments
                List<ItemStack> remainingItems = new ArrayList<>();
                for (ItemStack item : failedItems.values()) {
                    remainingItems.add(item);
                }
                message.setAttachments(remainingItems);
            }
        }

        return true;
    }

}
