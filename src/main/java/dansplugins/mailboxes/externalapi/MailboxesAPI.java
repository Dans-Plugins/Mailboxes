package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.services.MailService;
import jdk.jfr.internal.Logger;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MailboxesAPI implements IMailboxesAPI {

    private String APIVersion = "v0.0.3";

    @Override
    public String getAPIVersion() {
        return APIVersion;
    }

    @Override
    public String getVersion() {
        return Mailboxes.getInstance().getVersion();
    }

    @Override
    public M_Mailbox getMailbox(Player player) {
        return new M_Mailbox(PersistentData.getInstance().getMailbox(player));
    }

    @Override
    public M_Message getMessage(int ID) {
        return new M_Message(PersistentData.getInstance().getMessage(ID));
    }

    @Override
    public boolean sendPluginMessageToPlayer(String pluginName, Player player, String content) {
        PluginMessage message = MessageFactory.getInstance().createPluginMessage(pluginName, player.getUniqueId(), content);
        return MailService.getInstance().sendMessage(message);
    }

    @Override
    public boolean sendPluginMessageToPlayer(String pluginName, UUID playerUUID, String content) {
        PluginMessage message = MessageFactory.getInstance().createPluginMessage(pluginName, playerUUID, content);
        return MailService.getInstance().sendMessage(message);
    }

}
