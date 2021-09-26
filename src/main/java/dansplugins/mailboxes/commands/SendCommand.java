package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.services.MailService;
import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class SendCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            // TODO: add message
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // TODO: send usage message
            return false;
        }

        ArrayList<String> singleQuoteArgs = ArgumentParser.getInstance().getArgumentsInsideSingleQuotes(args);

        if (singleQuoteArgs.size() < 2) {
            // TODO: send single quotes requirement
            return false;
        }

        String recipientName = singleQuoteArgs.get(0);
        UUID recipientUUID = UUIDChecker.getInstance().findUUIDBasedOnPlayerName(recipientName);

        if (recipientUUID == null) {
            // TODO: add message
            return false;
        }

        String messageContent = singleQuoteArgs.get(1);

        Message message = MessageFactory.getInstance().createPlayerMessage(player.getUniqueId(), recipientUUID, messageContent);

        return MailService.getInstance().sendMessage(message);
    }

}
