package dansplugins.mailboxes.utils;

import java.util.function.BooleanSupplier;

public class Logger {
    private final BooleanSupplier debugEnabled;

    /**
     * @param debugEnabled consulted on every {@link #log(String)} call, so a change to the
     *                     {@code debugMode} config option takes effect without a restart
     */
    public Logger(BooleanSupplier debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void log(String message) {
        if (debugEnabled.getAsBoolean()) {
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
