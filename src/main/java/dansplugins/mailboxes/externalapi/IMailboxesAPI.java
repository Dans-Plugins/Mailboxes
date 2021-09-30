package dansplugins.mailboxes.externalapi;

import org.bukkit.entity.Player;

public interface IMailboxesAPI {
    String getAPIVersion();
    String getVersion();
    M_Mailbox getMailbox(Player player);
    M_Message getMessage(int ID);
    boolean sendPluginMessageToPlayer(String pluginName, Player player, String content);
}
