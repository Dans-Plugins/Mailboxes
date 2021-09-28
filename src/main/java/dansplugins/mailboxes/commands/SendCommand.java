package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.PlayerMessage;
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

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Logger.getInstance().log("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /m send (playerName) 'message'");
            return false;
        }

        String recipientName = args[0];
        UUID recipientUUID = UUIDChecker.getInstance().findUUIDBasedOnPlayerName(recipientName);

        if (recipientUUID == null) {
            player.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        ArrayList<String> singleQuoteArgs = ArgumentParser.getInstance().getArgumentsInsideSingleQuotes(args);

        if (singleQuoteArgs.size() < 1) {
            player.sendMessage(ChatColor.RED + "Message must be designated between single quotes.");
            return false;
        }

        String messageContent = singleQuoteArgs.get(0);

        PlayerMessage message = MessageFactory.getInstance().createPlayerMessage(player.getUniqueId(), recipientUUID, messageContent);

        return MailService.getInstance().sendMessage(message);
    }

}
