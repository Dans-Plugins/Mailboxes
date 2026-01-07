package dansplugins.mailboxes;

import dansplugins.mailboxes.bstats.Metrics;
import dansplugins.mailboxes.commands.*;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.services.*;
import dansplugins.mailboxes.utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Mailboxes extends JavaPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final ConfigService configService = new ConfigService(this);
    private final Logger logger = new Logger(this);
    private final PersistentData persistentData = new PersistentData(logger);
    private final StorageService storageService = new StorageService(configService, this, persistentData, logger);
    private final MailboxFactory mailboxFactory = new MailboxFactory(configService, persistentData, logger);
    private final MailService mailService = new MailService(this, logger, persistentData.getLookupService());
    private final MailboxService mailboxService = new MailboxService(persistentData, mailboxFactory, configService, mailService, logger);
    private final EventRegistry eventRegistry = new EventRegistry(this, mailboxService);
    private final Scheduler scheduler = new Scheduler(logger, this, storageService);
    private final UUIDChecker uuidChecker = new UUIDChecker();
    private final MessageFactory messageFactory = new MessageFactory(uuidChecker, configService, persistentData, logger);
    private final MailboxesAPI API = new MailboxesAPI(this, persistentData, messageFactory, mailService);
    private final ArgumentParser argumentParser = new ArgumentParser();
    private final PermissionChecker permissionChecker = new PermissionChecker();

    @Override
    public void onEnable() {
        // bStats
        int pluginId = 12902;
        Metrics metrics = new Metrics(this, pluginId);

        // create/load config
        if (!(new File("./plugins/Mailboxes/config.yml").exists())) {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                configService.saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        storageService.load();

        eventRegistry.registerEvents();

        scheduler.scheduleAutosave();
    }

    @Override
    public void onDisable() {
        storageService.save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter(this, argumentParser, permissionChecker, configService, logger, persistentData, uuidChecker, messageFactory, mailService);
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean isDebugEnabled() {
        return configService.getBoolean("debugMode");
    }

    public MailboxesAPI getAPI() {
        return API;
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("mailboxes") || label.equalsIgnoreCase("m")) {
            return getTabCompletions(sender, args);
        }
        return null;
    }

    private List<String> getTabCompletions(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Main subcommands - filter by permissions
            List<String> subcommands = new ArrayList<>();
            if (sender.hasPermission("mailboxes.help")) subcommands.add("help");
            if (sender.hasPermission("mailboxes.config")) subcommands.add("config");
            if (sender.hasPermission("mailboxes.list")) subcommands.add("list");
            if (sender.hasPermission("mailboxes.send")) subcommands.add("send");
            if (sender.hasPermission("mailboxes.open")) subcommands.add("open");
            if (sender.hasPermission("mailboxes.delete")) subcommands.add("delete");
            if (sender.hasPermission("mailboxes.archive")) subcommands.add("archive");
            return filterCompletions(subcommands, args[0]);
        }

        if (args.length >= 2) {
            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "config":
                    if (sender.hasPermission("mailboxes.config")) {
                        ConfigCommand configCommand = new ConfigCommand(configService);
                        return configCommand.getTabCompletions(args);
                    }
                    break;
                case "list":
                    if (sender.hasPermission("mailboxes.list")) {
                        ListCommand listCommand = new ListCommand(logger, persistentData);
                        return listCommand.getTabCompletions(args);
                    }
                    break;
                case "send":
                    if (sender.hasPermission("mailboxes.send")) {
                        SendCommand sendCommand = new SendCommand(logger, uuidChecker, configService, argumentParser, messageFactory, mailService);
                        return sendCommand.getTabCompletions(sender, args);
                    }
                    break;
                case "open":
                    if (sender.hasPermission("mailboxes.open")) {
                        OpenCommand openCommand = new OpenCommand(logger, persistentData);
                        return openCommand.getTabCompletions(sender, args);
                    }
                    break;
                case "delete":
                    if (sender.hasPermission("mailboxes.delete")) {
                        DeleteCommand deleteCommand = new DeleteCommand(persistentData);
                        return deleteCommand.getTabCompletions(sender, args);
                    }
                    break;
                case "archive":
                    if (sender.hasPermission("mailboxes.archive")) {
                        ArchiveCommand archiveCommand = new ArchiveCommand(persistentData);
                        return archiveCommand.getTabCompletions(sender, args);
                    }
                    break;
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