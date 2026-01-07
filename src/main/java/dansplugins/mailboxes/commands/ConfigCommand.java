package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.services.ConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigCommand {
    private final ConfigService configService;

    // Config option names for tab completion (in camelCase to match config file)
    // Note: 'version' is intentionally excluded as it cannot be set by users
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
    // These are derived from CONFIG_OPTIONS to maintain consistency
    private static final Set<String> BOOLEAN_CONFIG_OPTIONS = new HashSet<>(Arrays.asList(
        "debugmode",
        "preventsendingmessagestoself",
        "assignmentalertenabled",
        "unreadmessagesalertenabled",
        "welcomemessageenabled",
        "quotesenabled",
        "attachmentsenabled"
    ));

    public ConfigCommand(ConfigService configService) {
        this.configService = configService;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
            return false;
        }

        if (args[0].equalsIgnoreCase("show")) {
            configService.sendConfigList(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /c config set (option) (value)");
                return false;
            }
            String option = args[1];

            String value = args[2];

            configService.setConfigOption(option, value, sender);
            return true;
        }
        else {
            sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
            return false;
        }
    }

    public List<String> getTabCompletions(String[] args) {
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

    private List<String> filterCompletions(List<String> options, String input) {
        String lowerInput = input.toLowerCase();
        return options.stream()
            .filter(option -> option.toLowerCase().startsWith(lowerInput))
            .collect(Collectors.toList());
    }

}
