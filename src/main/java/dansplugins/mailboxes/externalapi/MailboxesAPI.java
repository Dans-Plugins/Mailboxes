package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import org.bukkit.entity.Player;

public class MailboxesAPI implements IMailboxesAPI {

    private String APIVersion = "v0.0.1";

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

}
