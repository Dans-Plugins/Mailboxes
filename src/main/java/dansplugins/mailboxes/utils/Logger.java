package dansplugins.mailboxes.utils;

import dansplugins.mailboxes.Mailboxes;

public class Logger {
    private final Mailboxes mailboxes;

    public Logger(Mailboxes mailboxes) {
        this.mailboxes = mailboxes;
    }

    public void log(String message) {
        if (mailboxes.isDebugEnabled()) {
            System.out.println("[Mailboxes] " + message);
        }
    }

}
