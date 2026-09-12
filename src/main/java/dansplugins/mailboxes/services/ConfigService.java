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
    /**
     * The smallest value accepted for an integer config option.
     *
     * Every integer option is either an exclusive upper bound handed to {@code Random.nextInt}
     * (which throws below one) or a stack size, so nothing below one is usable.
     */
    private static final int MINIMUM_INTEGER_OPTION_VALUE = 1;

    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

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
        saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        if (getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("maxMessageIDNumber")
                    || option.equalsIgnoreCase("maxMailboxIDNumber")
                    || option.equalsIgnoreCase("maxAttachmentStackSize")) {
                Integer parsedValue = parseIntegerOptionValue(option, value, sender);
                if (parsedValue == null) {
                    return;
                }
                getConfig().set(option, parsedValue);
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
            saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    /**
     * Parses and range-checks the value given for an integer config option.
     *
     * Storing an unchecked value used to be enough to disable the plugin server-wide: a zero or
     * negative maxMessageIDNumber makes every message creation throw out of
     * {@code Random.nextInt}, and a non-numeric value threw a NumberFormatException at the
     * command sender instead of a usage message.
     *
     * @return the validated value, or null if it was rejected — in which case the sender has
     *         already been told why and the option must be left unchanged
     */
    private Integer parseIntegerOptionValue(String option, String value, CommandSender sender) {
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The value given for " + option + " must be a whole number.");
            return null;
        }
        if (parsedValue < MINIMUM_INTEGER_OPTION_VALUE) {
            sender.sendMessage(ChatColor.RED + option + " must be at least " + MINIMUM_INTEGER_OPTION_VALUE + ".");
            return null;
        }
        return parsedValue;
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

    /**
     * Persists the configuration. Kept alongside {@link #getConfig()} as the second seam onto the
     * plugin instance, so that tests can exercise a config change without a live Mailboxes.
     */
    void saveConfig() {
        mailboxes.saveConfig();
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

    // The one-argument getters, deliberately. saveDefaultConfig() never touches a
    // config.yml that already exists, and saveMissingConfigDefaultsIfNotPresent()
    // only runs when the file is missing or the version changed, so a server
    // upgraded from a version before usage reporting has no usage-reporting block
    // on disk. Bukkit registers the jar's config.yml as the defaults for that
    // file, and the one-argument getters fall through to them -- but the
    // two-argument getters return their explicit fallback instead, which for the
    // key would be "" and would turn reporting off on every existing
    // installation. Verified against YamlConfiguration, not assumed.

    public boolean isUsageReportingEnabled() {
        return getConfig().getBoolean(USAGE_REPORTING_ENABLED_KEY);
    }

    public String getUsageReportingEndpoint() {
        String endpoint = getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint != null ? endpoint : DEFAULT_USAGE_REPORTING_ENDPOINT;
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        String key = getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key != null ? key : "";
    }

}