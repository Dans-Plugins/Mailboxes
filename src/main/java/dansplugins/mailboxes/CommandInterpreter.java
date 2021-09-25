package dansplugins.mailboxes;

import dansplugins.mailboxes.commands.HelpCommand;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.PermissionChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("mailboxes") || label.equalsIgnoreCase("m")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "Mailboxes " + Mailboxes.getInstance().getVersion());
                sender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Mailboxes/wiki");
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = ArgumentParser.getInstance().dropFirstArgument(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                if (!PermissionChecker.getInstance().checkPermission(sender, "Mailboxes.help")) { return false; }
                HelpCommand command = new HelpCommand();
                return command.execute(sender);
            }

            sender.sendMessage(ChatColor.RED + "Mailboxes doesn't recognize that command.");
        }
        return false;
    }

}