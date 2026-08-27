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

    /**
     * Logs a failure that a server owner needs to see. Unlike {@link #log(String)}, this is not
     * gated behind debug mode.
     *
     * @param message the message to log
     */
    public void logError(String message) {
        System.out.println("[Mailboxes] [ERROR] " + message);
    }

}
