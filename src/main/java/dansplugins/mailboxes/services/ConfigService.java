package dansplugins.mailboxes.services;

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

public class ConfigService {
    private final Mailboxes mailboxes;

    private boolean altered = false;

    public ConfigService(Mailboxes mailboxes) {
        this.mailboxes = mailboxes;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", mailboxes.getVersion());
        }
        else {
            getConfig().set("version", mailboxes.getVersion());
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
            getConfig().set("assignmentAlertEnabled", false);
        }
        if (!getConfig().isSet("unreadMessagesAlertEnabled")) {
            getConfig().set("unreadMessagesAlertEnabled", true);
        }
        if (!getConfig().isSet("welcomeMessageEnabled")) {
            getConfig().set("welcomeMessageEnabled", true);
        }
        if (!getConfig().isSet("quotesEnabled")) {
            getConfig().set("quotesEnabled", false);
        }
        if (!getConfig().isSet("attachmentsEnabled")) {
            getConfig().set("attachmentsEnabled", true);
        }
        if (!getConfig().isSet("maxAttachmentStackSize")) {
            getConfig().set("maxAttachmentStackSize", 64);
        }
        getConfig().options().copyDefaults(true);
        mailboxes.saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        if (getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("maxMessageIDNumber")
                    || option.equalsIgnoreCase("maxMailboxIDNumber")
                    || option.equalsIgnoreCase("maxAttachmentStackSize")) {
                getConfig().set(option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("preventSendingMessagesToSelf")
                    || option.equalsIgnoreCase("assignmentAlertEnabled")
                    || option.equalsIgnoreCase("unreadMessagesAlertEnabled")
                    || option.equalsIgnoreCase("welcomeMessageEnabled")
                    || option.equalsIgnoreCase("quotesEnabled")
                    || option.equalsIgnoreCase("attachmentsEnabled")) {
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
            mailboxes.saveConfig();
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
                + ", quotesEnabled: " + getBoolean("quotesEnabled")
                + ", attachmentsEnabled: " + getBoolean("attachmentsEnabled")
                + ", maxAttachmentStackSize: " + getInt("maxAttachmentStackSize"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return mailboxes.getConfig();
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