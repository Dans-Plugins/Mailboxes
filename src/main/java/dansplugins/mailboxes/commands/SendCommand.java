package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.services.MailService;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SendCommand {
    private final Logger logger;
    private final UUIDChecker uuidChecker;
    private final ConfigService configService;
    private final ArgumentParser argumentParser;
    private final MessageFactory messageFactory;
    private final MailService mailService;

    public SendCommand(Logger logger, UUIDChecker uuidChecker, ConfigService configService, ArgumentParser argumentParser, MessageFactory messageFactory, MailService mailService) {
        this.logger = logger;
        this.uuidChecker = uuidChecker;
        this.configService = configService;
        this.argumentParser = argumentParser;
        this.messageFactory = messageFactory;
        this.mailService = mailService;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            logger.log("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /m send (playerName) 'message' [-attach]");
            return false;
        }

        String recipientName = args[0];
        UUID recipientUUID = uuidChecker.findUUIDBasedOnPlayerName(recipientName);

        if (recipientUUID == null) {
            player.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        if (configService.getBoolean("preventSendingMessagesToSelf")) {
            if (recipientUUID.equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You can't send a message to yourself.");
                return false;
            }
        }

        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);

        if (doubleQuoteArgs.size() < 1) {
            player.sendMessage(ChatColor.RED + "Message must be designated between double quotes.");
            return false;
        }

        String messageContent = doubleQuoteArgs.get(0);

        // Check if attachments flag is present
        boolean shouldAttach = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-attach")) {
                shouldAttach = true;
                break;
            }
        }

        PlayerMessage message = messageFactory.createPlayerMessage(player.getUniqueId(), recipientUUID, messageContent);

        // Handle attachments
        if (shouldAttach) {
            if (!player.hasPermission("mailboxes.send.attach")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to send attachments.");
                return false;
            }
            
            if (!configService.getBoolean("attachmentsEnabled")) {
                player.sendMessage(ChatColor.RED + "Attachments are currently disabled by the server.");
                return false;
            }
            
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            
            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You must hold an item in your hand to attach it.");
                return false;
            }

            // Check max stack size limit
            int maxStackSize = configService.getInt("maxAttachmentStackSize");
            if (itemInHand.getAmount() > maxStackSize) {
                player.sendMessage(ChatColor.RED + "Item stack size exceeds the maximum allowed (" + maxStackSize + ").");
                return false;
            }

            // Clone the item and add as attachment
            ItemStack attachment = itemInHand.clone();
            message.addAttachment(attachment);

            // Remove item from player's hand
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            
            player.sendMessage(ChatColor.GREEN + "Attached " + attachment.getAmount() + "x " + 
                             attachment.getType().toString().toLowerCase().replace("_", " "));
        }

        mailService.sendMessage(message);
        player.sendMessage(ChatColor.GREEN + "Sent.");
        return true;
    }

    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            // Player names (both online and offline) only at position 2
            // Use Set to avoid duplicates (online players also appear in offline list)
            Set<String> playerNames = new HashSet<>();
            
            // Add online players
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            
            // Add offline players (online players will be deduplicated by Set)
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                if (player.getName() != null) {
                    playerNames.add(player.getName());
                }
            }
            
            return filterCompletions(new ArrayList<>(playerNames), args[1]);
        }
        // Suggest -attach flag for subsequent positions if not already present
        if (args.length >= 3) {
            return getAttachFlagSuggestion(sender, args, args[args.length - 1]);
        }
        return new ArrayList<>();
    }

    private List<String> getAttachFlagSuggestion(CommandSender sender, String[] args, String input) {
        // Check if -attach flag is already present
        boolean hasAttachFlag = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("-attach"));
        if (!hasAttachFlag && sender.hasPermission("mailboxes.send.attach")) {
            return filterCompletions(Arrays.asList("-attach"), input);
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
