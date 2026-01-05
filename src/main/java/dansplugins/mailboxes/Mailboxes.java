package dansplugins.mailboxes;

import dansplugins.mailboxes.bstats.Metrics;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.services.*;
import dansplugins.mailboxes.utils.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Mailboxes extends JavaPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    // Config option names for tab completion (in camelCase to match config file)
    private static final List<String> CONFIG_OPTIONS = Arrays.asList(
        "debugMode",
        "maxMessageIDNumber",
        "maxMailboxIDNumber",
        "preventSendingMessagesToSelf",
        "assignmentAlertEnabled",
        "unreadMessagesAlertEnabled",
        "welcomeMessageEnabled",
        "quotesEnabled",
        "attachmentsEnabled",
        "maxAttachmentStackSize"
    );

    // Boolean config options (stored in lowercase for case-insensitive comparison)
    private static final Set<String> BOOLEAN_CONFIG_OPTIONS = new HashSet<>(Arrays.asList(
        "debugmode",
        "preventsendingmessagestoself",
        "assignmentalertenabled",
        "unreadmessagesalertenabled",
        "welcomemessageenabled",
        "quotesenabled",
        "attachmentsenabled"
    ));

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
            // Main subcommands
            List<String> subcommands = Arrays.asList("help", "config", "list", "send", "open", "delete", "archive");
            return filterCompletions(subcommands, args[0]);
        }

        if (args.length >= 2) {
            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "config":
                    return getConfigCompletions(args);
                case "list":
                    return getListCompletions(args);
                case "send":
                    return getSendCompletions(sender, args);
            }
        }

        return new ArrayList<>();
    }

    private List<String> getConfigCompletions(String[] args) {
        if (args.length == 2) {
            // Config subcommands
            return filterCompletions(Arrays.asList("show", "set"), args[1]);
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
            // Config options
            return filterCompletions(CONFIG_OPTIONS, args[2]);
        }
        if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
            // Suggest values based on the config option type
            String option = args[2];
            if (BOOLEAN_CONFIG_OPTIONS.contains(option.toLowerCase())) {
                return filterCompletions(Arrays.asList("true", "false"), args[3]);
            }
        }
        return new ArrayList<>();
    }

    private List<String> getListCompletions(String[] args) {
        if (args.length == 2) {
            return filterCompletions(Arrays.asList("active", "archived", "unread"), args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> getSendCompletions(CommandSender sender, String[] args) {
        if (args.length == 2) {
            // Player names and -attach flag
            List<String> completions = filterCompletions(
                Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()),
                args[1]
            );
            // Also suggest -attach flag if user has permission
            completions.addAll(getAttachFlagSuggestion(sender, args, args[1]));
            return completions;
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
        return options.stream()
            .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
            .collect(Collectors.toList());
    }
}