package dansplugins.mailboxes.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dansplugins.mailboxes.services.MailboxService;

public class JoinListener implements Listener {
    private final MailboxService mailboxService;

    public JoinListener(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        mailboxService.assignMailboxToPlayerIfNecessary(player);
        mailboxService.alertPlayerIfTheyHaveUnreadMessages(player);
    }

}
