package dansplugins.mailboxes.utils;

import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.services.StorageService;
import org.bukkit.Bukkit;

public class Scheduler {
    private final Logger logger;
    private final Mailboxes mailboxes;
    private final StorageService storageService;

    public Scheduler(Logger logger, Mailboxes mailboxes, StorageService storageService) {
        this.logger = logger;
        this.mailboxes = mailboxes;
        this.storageService = storageService;
    }

    public void scheduleAutosave() {
        logger.log("[Currencies] Scheduling hourly autosave.");
        int delay = 60 * 60; // 1 hour
        int secondsUntilRepeat = 60 * 60; // 1 hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(mailboxes, new Runnable() {
            @Override
            public void run() {
                logger.log("[Mailboxes] Saving. This will happen hourly.");
                storageService.save();
            }
        }, delay * 20, secondsUntilRepeat * 20);
    }

}
