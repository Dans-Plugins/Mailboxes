package dansplugins.mailboxes.utils;

import dansplugins.mailboxes.Mailboxes;

public class Logger {

    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(String message) {
        if (Mailboxes.getInstance().isDebugEnabled()) {
            System.out.println("[Currencies] " + message);
        }
    }

}
