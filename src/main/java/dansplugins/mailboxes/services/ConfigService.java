package dansplugins.mailboxes.services;

import dansplugins.mailboxes.Mailboxes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - replaceUnusableConfigValues()
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

    private static final boolean DEFAULT_DEBUG_MODE = false;
    private static final int DEFAULT_MAX_MESSAGE_ID_NUMBER = 10000;
    private static final int DEFAULT_MAX_MAILBOX_ID_NUMBER = 10000;
    private static final boolean DEFAULT_PREVENT_SENDING_MESSAGES_TO_SELF = true;
    private static final boolean DEFAULT_ASSIGNMENT_ALERT_ENABLED = false;
    private static final boolean DEFAULT_UNREAD_MESSAGES_ALERT_ENABLED = true;
    private static final boolean DEFAULT_WELCOME_MESSAGE_ENABLED = true;
    private static final boolean DEFAULT_QUOTES_ENABLED = false;
    private static final boolean DEFAULT_ATTACHMENTS_ENABLED = true;
    private static final int DEFAULT_MAX_ATTACHMENT_STACK_SIZE = 64;

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
            getConfig().set("debugMode", DEFAULT_DEBUG_MODE);
        }
        if (!getConfig().isSet("maxMessageIDNumber")) {
            getConfig().set("maxMessageIDNumber", DEFAULT_MAX_MESSAGE_ID_NUMBER);
        }
        if (!getConfig().isSet("maxMailboxIDNumber")) {
            getConfig().set("maxMailboxIDNumber", DEFAULT_MAX_MAILBOX_ID_NUMBER);
        }
        if (!getConfig().isSet("preventSendingMessagesToSelf")) {
            getConfig().set("preventSendingMessagesToSelf", DEFAULT_PREVENT_SENDING_MESSAGES_TO_SELF);
        }
        if (!getConfig().isSet("assignmentAlertEnabled")) {
            getConfig().set("assignmentAlertEnabled", DEFAULT_ASSIGNMENT_ALERT_ENABLED);
        }
        if (!getConfig().isSet("unreadMessagesAlertEnabled")) {
            getConfig().set("unreadMessagesAlertEnabled", DEFAULT_UNREAD_MESSAGES_ALERT_ENABLED);
        }
        if (!getConfig().isSet("welcomeMessageEnabled")) {
            getConfig().set("welcomeMessageEnabled", DEFAULT_WELCOME_MESSAGE_ENABLED);
        }
        if (!getConfig().isSet("quotesEnabled")) {
            getConfig().set("quotesEnabled", DEFAULT_QUOTES_ENABLED);
        }
        if (!getConfig().isSet("attachmentsEnabled")) {
            getConfig().set("attachmentsEnabled", DEFAULT_ATTACHMENTS_ENABLED);
        }
        if (!getConfig().isSet("maxAttachmentStackSize")) {
            getConfig().set("maxAttachmentStackSize", DEFAULT_MAX_ATTACHMENT_STACK_SIZE);
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Range-checks the values loaded from config.yml and replaces each unusable one with its
     * default, logging a warning that names the option and both values.
     *
     * Only {@link #setConfigOption} used to be validated, so a config.yml edited by hand (or
     * written before that validation existed) could still hold {@code maxMessageIDNumber: 0},
     * which throws out of {@code Random.nextInt} on the first attempt to create a message. A
     * value the file cannot be read as an integer lands in the same place, because
     * {@code FileConfiguration.getInt} returns zero for it. Startup is where this is checked,
     * because a console warning at boot names the cause where a stack trace on the first
     * {@code /m send} does not.
     *
     * This runs on every enable, not only when the defaults are written, because the defaults
     * are only written to a file that is absent or out of date.
     */
    public void replaceUnusableConfigValues() {
        boolean replaced = false;
        replaced |= replaceUnusableIntegerValue("maxMessageIDNumber", DEFAULT_MAX_MESSAGE_ID_NUMBER);
        replaced |= replaceUnusableIntegerValue("maxMailboxIDNumber", DEFAULT_MAX_MAILBOX_ID_NUMBER);
        replaced |= replaceUnusableIntegerValue("maxAttachmentStackSize", DEFAULT_MAX_ATTACHMENT_STACK_SIZE);
        replaced |= replaceUnusableBooleanValue("debugMode", DEFAULT_DEBUG_MODE);
        replaced |= replaceUnusableBooleanValue("preventSendingMessagesToSelf", DEFAULT_PREVENT_SENDING_MESSAGES_TO_SELF);
        replaced |= replaceUnusableBooleanValue("assignmentAlertEnabled", DEFAULT_ASSIGNMENT_ALERT_ENABLED);
        replaced |= replaceUnusableBooleanValue("unreadMessagesAlertEnabled", DEFAULT_UNREAD_MESSAGES_ALERT_ENABLED);
        replaced |= replaceUnusableBooleanValue("welcomeMessageEnabled", DEFAULT_WELCOME_MESSAGE_ENABLED);
        replaced |= replaceUnusableBooleanValue("quotesEnabled", DEFAULT_QUOTES_ENABLED);
        replaced |= replaceUnusableBooleanValue("attachmentsEnabled", DEFAULT_ATTACHMENTS_ENABLED);
        if (replaced) {
            saveConfig();
        }
    }

    /**
     * @return true if the option held a value that is not an integer, or one below
     *         {@link #MINIMUM_INTEGER_OPTION_VALUE}, and was replaced with its default
     */
    private boolean replaceUnusableIntegerValue(String option, int defaultValue) {
        if (!getConfig().isSet(option)) {
            return false;
        }
        if (getConfig().isInt(option) && getConfig().getInt(option) >= MINIMUM_INTEGER_OPTION_VALUE) {
            return false;
        }
        Object loadedValue = getConfig().get(option);
        getConfig().set(option, defaultValue);
        logWarning(option + " is set to " + loadedValue + " in config.yml, which is not a whole number of at least "
                + MINIMUM_INTEGER_OPTION_VALUE + "; the default of " + defaultValue + " has been used instead.");
        return true;
    }

    /**
     * @return true if the option held a value that is not a boolean and was replaced with its
     *         default
     */
    private boolean replaceUnusableBooleanValue(String option, boolean defaultValue) {
        if (!getConfig().isSet(option) || getConfig().isBoolean(option)) {
            return false;
        }
        Object loadedValue = getConfig().get(option);
        getConfig().set(option, defaultValue);
        logWarning(option + " is set to " + loadedValue + " in config.yml, which is not true or false; the default of "
                + defaultValue + " has been used instead.");
        return true;
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
                Boolean parsedValue = parseBooleanOptionValue(option, value, sender);
                if (parsedValue == null) {
                    return;
                }
                getConfig().set(option, parsedValue);
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

    /**
     * Parses the value given for a boolean config option, accepting only {@code true} and
     * {@code false} in any case.
     *
     * {@code Boolean.parseBoolean} used to be called directly, and it returns false for every
     * string other than "true", so {@code yes}, {@code 1} and a typo of {@code true} were all
     * stored as false under a "Boolean set." success message. Tab completion offers only
     * {@code true} and {@code false}, which is the accepted set.
     *
     * @return the parsed value, or null if it was rejected — in which case the sender has
     *         already been told why and the option must be left unchanged
     */
    private Boolean parseBooleanOptionValue(String option, String value, CommandSender sender) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        sender.sendMessage(ChatColor.RED + "The value given for " + option + " must be true or false.");
        return null;
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

    /**
     * Warns on the server console regardless of debug mode. The plugin's own {@code Logger} is
     * constructed after this service and gates everything but errors behind debug mode, so the
     * server logger is used directly; kept as a seam for the same reason as {@link #saveConfig()}.
     */
    void logWarning(String message) {
        mailboxes.getLogger().warning(message);
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

    // The one-argument getters, deliberately. Bukkit registers the jar's
    // config.yml as the defaults for the file on disk, and the one-argument
    // getters fall through to them for any key the file lacks -- the
    // two-argument getters return their explicit fallback instead, which for
    // the key would be "" and would read as "off". Mailboxes#onEnable rewrites
    // a config.yml that lacks the usage-reporting block (copyDefaults(true) in
    // saveMissingConfigDefaultsIfNotPresent copies it in), so on a normal enable
    // the file has the keys; the fall-through only matters if that write
    // failed. Verified against YamlConfiguration, not assumed.

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