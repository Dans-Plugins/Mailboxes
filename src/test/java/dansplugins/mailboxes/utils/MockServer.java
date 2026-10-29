package dansplugins.mailboxes.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

import static org.mockito.Mockito.*;

/**
 * Bukkit.setServer can only be called once per JVM, so every test that reads
 * the static Bukkit server shares this one mock and stubs what it needs.
 */
final class MockServer {

    static final Server SERVER = mock(Server.class);
    static final BukkitScheduler SCHEDULER = mock(BukkitScheduler.class);

    static {
        when(SERVER.getLogger()).thenReturn(java.util.logging.Logger.getLogger("MockServer"));
        when(SERVER.getScheduler()).thenReturn(SCHEDULER);
    }

    private MockServer() {
    }

    /**
     * Installs the shared mock as the Bukkit server if no server is installed yet.
     *
     * @return whether the installed Bukkit server is the shared mock
     */
    static boolean install() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(SERVER);
        }
        return Bukkit.getServer() == SERVER;
    }
}
