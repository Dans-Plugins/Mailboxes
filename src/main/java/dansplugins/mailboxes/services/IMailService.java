package dansplugins.mailboxes.services;

import dansplugins.mailboxes.objects.Message;

public interface IMailService {
    boolean sendMessage(Message message);
}