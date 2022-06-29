package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.services.MailService;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MailboxesAPI {
    private final Mailboxes mailboxes;
    private final PersistentData persistentData;
    private final MessageFactory messageFactory;
    private final MailService mailService;

    private String APIVersion = "v0.0.3";

    public MailboxesAPI(Mailboxes mailboxes, PersistentData persistentData, MessageFactory messageFactory, MailService mailService) {
        this.mailboxes = mailboxes;
        this.persistentData = persistentData;
        this.messageFactory = messageFactory;
        this.mailService = mailService;
    }

    public String getAPIVersion() {
        return APIVersion;
    }

    public String getVersion() {
        return mailboxes.getVersion();
    }

    public M_Mailbox getMailbox(Player player) {
        return new M_Mailbox(persistentData.getMailbox(player));
    }

    public M_Message getMessage(int ID) {
        return new M_Message(persistentData.getMessage(ID));
    }

    public boolean sendPluginMessageToPlayer(String pluginName, Player player, String content) {
        PluginMessage message = messageFactory.createPluginMessage(pluginName, player.getUniqueId(), content);
        return mailService.sendMessage(message);
    }

    public boolean sendPluginMessageToPlayer(String pluginName, UUID playerUUID, String content) {
        PluginMessage message = messageFactory.createPluginMessage(pluginName, playerUUID, content);
        return mailService.sendMessage(message);
    }

}
