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
        String messageType;
        String messageSender;
        String messageRecipient;
        String messageContent;

        if (!(sender instanceof Player)) {
            messageType = "ConsoleMessage";
            messageSender = "Console";
        }
        else {
            Player player = (Player) sender;
            messageType = "PlayerMessage";
            messageSender = player.getName();
        }

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
        messageRecipient = recipientUUID.toString();
        messageContent = singleQuoteArgs.get(1);

        Message message = MessageFactory.getInstance().createMessage(messageType, messageSender, messageRecipient, messageContent);

        return MailService.getInstance().sendMessage(message);
    }

}
