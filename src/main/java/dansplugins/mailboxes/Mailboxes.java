package dansplugins.mailboxes;

import org.bukkit.plugin.java.JavaPlugin;

public final class Mailboxes extends JavaPlugin {

    private static Mailboxes instance;

    public static Mailboxes getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    public boolean isDebugEnabled() {
        return true;
    }
}