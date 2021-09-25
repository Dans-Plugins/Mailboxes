package dansplugins.mailboxes;

import dansplugins.mailboxes.eventhandlers.JoinHandler;
import org.bukkit.plugin.PluginManager;

public class EventRegistry {

    private static EventRegistry instance;

    private EventRegistry() {

    }

    public static EventRegistry getInstance() {
        if (instance == null) {
            instance = new EventRegistry();
        }
        return instance;
    }

    public void registerEvents() {

        Mailboxes mainInstance = Mailboxes.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        // blocks and interaction
        manager.registerEvents(new JoinHandler(), mainInstance);
    }
    
}
