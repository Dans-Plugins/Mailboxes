package dansplugins.mailboxes.utils;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.listeners.JoinListener;

import dansplugins.mailboxes.services.MailboxService;
import org.bukkit.plugin.PluginManager;

public class EventRegistry {
    private final Mailboxes mailboxes;
    private final MailboxService mailboxService;
    public EventRegistry(Mailboxes mailboxes, MailboxService mailboxService) {
        this.mailboxes = mailboxes;
        this.mailboxService = mailboxService;
    }

    public void registerEvents() {
        Mailboxes mainInstance = mailboxes;
        PluginManager manager = mainInstance.getServer().getPluginManager();

        // blocks and interaction
        manager.registerEvents(new JoinListener(mailboxService), mainInstance);
    }
    
}
