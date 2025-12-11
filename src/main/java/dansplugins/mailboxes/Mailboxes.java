package dansplugins.mailboxes;

import dansplugins.mailboxes.bstats.Metrics;
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

public final class Mailboxes extends JavaPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final ConfigService configService = new ConfigService(this);
    private final Logger logger = new Logger(this);
    private final PersistentData persistentData = new PersistentData(logger);
    private final StorageService storageService = new StorageService(configService, this, persistentData, logger);
    private final MailboxFactory mailboxFactory = new MailboxFactory(configService, persistentData, logger);
    private final MailService mailService = new MailService(this, logger, persistentData.getLookupService());
    private final MailboxService mailboxService = new MailboxService(persistentData, mailboxFactory, configService, mailService, logger);
    private TopicService topicService;
    private RestApiService restApiService;
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

        // Initialize TopicService after loading data to ensure ID counters are correct
        topicService = new TopicService(persistentData, logger);

        eventRegistry.registerEvents();

        scheduler.scheduleAutosave();

        // Start REST API if enabled
        if (configService.getBoolean("apiEnabled")) {
            try {
                restApiService = new RestApiService(topicService, logger, configService);
                restApiService.start();
            } catch (Exception e) {
                logger.log("Failed to start REST API: " + e.getMessage());
                getLogger().severe("Failed to start REST API: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDisable() {
        // Stop REST API if running
        if (restApiService != null) {
            restApiService.stop();
        }
        
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
}