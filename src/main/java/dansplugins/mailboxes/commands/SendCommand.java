package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.services.MailService;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

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
            player.sendMessage(ChatColor.RED + "Usage: /m send (playerName) 'message'");
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

        PlayerMessage message = messageFactory.createPlayerMessage(player.getUniqueId(), recipientUUID, messageContent);
        mailService.sendMessage(message);
        player.sendMessage(ChatColor.GREEN + "Sent.");
        return true;
    }

}
