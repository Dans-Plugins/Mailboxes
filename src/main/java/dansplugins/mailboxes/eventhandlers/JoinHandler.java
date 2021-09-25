package dansplugins.mailboxes.eventhandlers;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.managers.MailboxManager;
import dansplugins.mailboxes.objects.Mailbox;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MailboxManager.getInstance().assignMailboxToPlayerIfNecessary(player);
    }

}
