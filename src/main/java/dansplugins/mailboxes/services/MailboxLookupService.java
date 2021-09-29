package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.utils.Logger;

import java.util.HashSet;
import java.util.UUID;

public class MailboxLookupService implements IMailboxLookupService {
    
    private static MailboxLookupService instance;
    private HashSet<Mailbox> cache = new HashSet<>();
    
    private MailboxLookupService() {
        
    }
    
    public static MailboxLookupService getInstance() {
        if (instance == null) {
            instance = new MailboxLookupService();
        }
        return instance;
    }

    @Override
    public Mailbox lookup(UUID playerUUID) {
        Logger.getInstance().log("Looking up mailbox for " + playerUUID.toString());
        Mailbox mailbox = checkCache(playerUUID);
        if (mailbox == null) {
            return checkStorage(playerUUID);
        }
        return mailbox;
    }

    private Mailbox checkCache(UUID playerUUID) {
        for (Mailbox mailbox : cache) {
            if (mailbox.getOwnerUUID().equals(playerUUID)) {
                Logger.getInstance().log("Found in cache!");
                return mailbox;
            }
        }
        return null;
    }

    private Mailbox checkStorage(UUID playerUUID) {
        Mailbox mailbox = PersistentData.getInstance().getMailbox(playerUUID);
        if (mailbox != null) {
            Logger.getInstance().log("Found in storage!");
            cache.add(mailbox);
        }
        else {
            Logger.getInstance().log("Not found.");
        }
        return mailbox;
    }
    
}
