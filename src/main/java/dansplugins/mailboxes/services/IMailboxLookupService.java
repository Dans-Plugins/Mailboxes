package dansplugins.mailboxes.services;

import dansplugins.mailboxes.objects.Mailbox;

import java.util.UUID;

public interface IMailboxLookupService {
    Mailbox lookup(UUID playerUUID);
}
