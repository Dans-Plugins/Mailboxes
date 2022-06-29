package dansplugins.mailboxes;

import dansplugins.mailboxes.commands.*;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.services.MailService;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.PermissionChecker;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {
    private final Mailboxes mailboxes;
    private final ArgumentParser argumentParser;
    private final PermissionChecker permissionChecker;
    private final ConfigService configService;
    private final Logger logger;
    private final PersistentData persistentData;
    private final UUIDChecker uuidChecker;
    private final MessageFactory messageFactory;
    private final MailService mailService;

    public CommandInterpreter(Mailboxes mailboxes, ArgumentParser argumentParser, PermissionChecker permissionChecker, ConfigService configService, Logger logger, PersistentData persistentData, UUIDChecker uuidChecker, MessageFactory messageFactory, MailService mailService) {
        this.mailboxes = mailboxes;
        this.argumentParser = argumentParser;
        this.permissionChecker = permissionChecker;
        this.configService = configService;
        this.logger = logger;
        this.persistentData = persistentData;
        this.uuidChecker = uuidChecker;
        this.messageFactory = messageFactory;
        this.mailService = mailService;
    }

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("mailboxes") || label.equalsIgnoreCase("m")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "Mailboxes " + mailboxes.getVersion());
                sender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Mailboxes/wiki");
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = argumentParser.dropFirstArgument(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.help")) { return false; }
                HelpCommand command = new HelpCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("config")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.config")) { return false; }
                ConfigCommand command = new ConfigCommand(configService);
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("list")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.list")) { return false; }
                ListCommand command = new ListCommand(logger, persistentData);
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("send")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.send")) { return false; }
                SendCommand command = new SendCommand(logger, uuidChecker, configService, argumentParser, messageFactory, mailService);
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("open")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.open")) { return false; }
                OpenCommand command = new OpenCommand(logger, persistentData);
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("delete")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.delete")) { return false; }
                DeleteCommand command = new DeleteCommand(persistentData);
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("archive")) {
                if (!permissionChecker.checkPermission(sender, "mailboxes.archive")) { return false; }
                ArchiveCommand command = new ArchiveCommand(persistentData);
                return command.execute(sender, arguments);
            }

            sender.sendMessage(ChatColor.RED + "Mailboxes doesn't recognize that command.");
        }
        return false;
    }

}