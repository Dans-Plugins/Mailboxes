package dansplugins.mailboxes;

import dansplugins.mailboxes.bstats.Metrics;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import dansplugins.mailboxes.managers.ConfigManager;
import dansplugins.mailboxes.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Mailboxes extends JavaPlugin {

    private static Mailboxes instance;

    private MailboxesAPI API = new MailboxesAPI();

    public static Mailboxes getInstance() {
        return instance;
    }

    private final String version = "v1.1-alpha-1";

    @Override
    public void onEnable() {
        instance = this;

        // bStats
        int pluginId = 12902;
        Metrics metrics = new Metrics(this, pluginId);

        // create/load config
        if (!(new File("./plugins/Mailboxes/config.yml").exists())) {
            ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        StorageManager.getInstance().load();

        EventRegistry.getInstance().registerEvents();

        Scheduler.getInstance().scheduleAutosave();
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter();
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return ConfigManager.getInstance().getBoolean("debugMode");
    }

    public MailboxesAPI getAPI() {
        return API;
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }
}