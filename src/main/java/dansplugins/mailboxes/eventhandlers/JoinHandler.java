package dansplugins.mailboxes.eventhandlers;

import dansplugins.mailboxes.data.PersistentData;
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

        if (PersistentData.getInstance().getMailbox(player.getUniqueId()) != null) {
            // player already has a mailbox
            return;
        }

        // player doesn't have a mailbox
        Mailbox newMailbox = new Mailbox(player.getUniqueId());
        PersistentData.getInstance().addMailbox(newMailbox);
        player.sendMessage(ChatColor.AQUA + "You have been assigned a mailbox. Type /m help for help."); // TODO: add config option for this message
    }

}
