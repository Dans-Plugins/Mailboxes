package dansplugins.mailboxes.managers;

import dansplugins.mailboxes.Mailboxes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

public class ConfigManager {

    private static ConfigManager instance;
    private boolean altered = false;

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", Mailboxes.getInstance().getVersion());
        }
        else {
            getConfig().set("version", Mailboxes.getInstance().getVersion());
        }

        // save config options
        if (!getConfig().isSet("debugMode")) {
            getConfig().set("debugMode", false);
        }
        if (!getConfig().isSet("maxMessageIDNumber")) {
            getConfig().set("maxMessageIDNumber", 10000);
        }
        if (!getConfig().isSet("maxMailboxIDNumber")) {
            getConfig().set("maxMailboxIDNumber", 10000);
        }
        if (!getConfig().isSet("preventSendingMessagesToSelf")) {
            getConfig().set("preventSendingMessagesToSelf", true);
        }
        if (!getConfig().isSet("assignmentAlertEnabled")) {
            getConfig().set("assignmentAlertEnabled", true);
        }
        if (!getConfig().isSet("unreadMessagesAlertEnabled")) {
            getConfig().set("unreadMessagesAlertEnabled", true);
        }
        if (!getConfig().isSet("welcomeMessageEnabled")) {
            getConfig().set("welcomeMessageEnabled", true);
        }
        if (!getConfig().isSet("quotesEnabled")) {
            getConfig().set("quotesEnabled", true);
        }
        getConfig().options().copyDefaults(true);
        Mailboxes.getInstance().saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        if (getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("maxMessageIDNumber")
                    || option.equalsIgnoreCase("maxMailboxIDNumber")) { // no integers yet
                getConfig().set(option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("preventSendingMessagesToSelf")
                    || option.equalsIgnoreCase("assignmentAlertEnabled")
                    || option.equalsIgnoreCase("unreadMessagesAlertEnabled")
                    || option.equalsIgnoreCase("welcomeMessageEnabled")
                    || option.equalsIgnoreCase("quotesEnabled")) {
                getConfig().set(option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("")) {
                getConfig().set(option, Double.parseDouble(value)); // no doubles yet
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            Mailboxes.getInstance().saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getString("debugMode")
                + ", maxMessageIDNumber: " + getInt("maxMessageIDNumber")
                + ", maxMailboxIDNumber: " + getInt("maxMailboxIDNumber")
                + ", preventSendingMessagesToSelf: " + getBoolean("preventSendingMessagesToSelf")
                + ", assignmentAlertEnabled: " + getBoolean("assignmentAlertEnabled")
                + ", unreadMessagesAlertEnabled: " + getBoolean("unreadMessagesAlertEnabled")
                + ", welcomeMessageEnabled: " + getBoolean("welcomeMessageEnabled")
                + ", quotesEnabled: " + getBoolean("quotesEnabled"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return Mailboxes.getInstance().getConfig();
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }

}