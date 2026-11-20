package dansplugins.mailboxes.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageService {
    private final ConfigService configService;
    private final Mailboxes mailboxes;
    private final PersistentData persistentData;
    private final Logger logger;

    private final static String FILE_PATH = "./plugins/Mailboxes/";
    private final static String MAILBOXES_FILE_NAME = "mailboxes.json";
    private final static String ACTIVE_MESSAGES_FILE_NAME = "activeMessages.json";
    private final static String ARCHIVED_MESSAGES_FILE_NAME = "archivedMessages.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    private final String filePath;

    // Cleared only by a successful load(). Until then save() refuses to run: the in-memory
    // mailboxes are empty (or incomplete), and writing them would replace the very files that
    // could not be read. The autosave and onDisable() both call save(), and Bukkit still runs
    // onDisable() for a plugin whose onEnable() threw.
    private boolean loadFailed = true;

    public StorageService(ConfigService configService, Mailboxes mailboxes, PersistentData persistentData, Logger logger) {
        this(configService, mailboxes, persistentData, logger, FILE_PATH);
    }

    StorageService(ConfigService configService, Mailboxes mailboxes, PersistentData persistentData, Logger logger, String filePath) {
        this.configService = configService;
        this.mailboxes = mailboxes;
        this.persistentData = persistentData;
        this.logger = logger;
        this.filePath = filePath;
    }

    /**
     * Writes every mailbox and message to the data folder. Refused, with the files left as they
     * were, until {@link #load()} has succeeded.
     * @return Whether all three files were written.
     */
    public boolean save() {
        if (loadFailed) {
            logger.logError("Mailboxes and messages were not saved because they were not loaded when the plugin "
                    + "was enabled. The files in " + filePath + " have been left as they were.");
            return false;
        }
        boolean saved = saveMailboxes();
        saved &= saveMessages(ACTIVE_MESSAGES_FILE_NAME);
        saved &= saveMessages(ARCHIVED_MESSAGES_FILE_NAME);
        // config.yml is not saved here: /m config set saves each change as it is made, and
        // saving the loaded config again on autosave or shutdown could write a stale copy over
        // edits the operator made by hand since, usage-reporting opt-out included.
        return saved;
    }

    /**
     * Reads every mailbox and message. All three files are read and parsed before anything in
     * {@link PersistentData} changes, so a file that cannot be read, or one malformed mailbox or
     * message, leaves it exactly as it was.
     * @return Whether the data was loaded. On failure the cause has been logged and
     *         {@link #save()} is refused until a later load succeeds.
     */
    public boolean load() {
        List<Mailbox> loadedMailboxes = new ArrayList<>();
        ArrayList<Message> activeMessages;
        ArrayList<Message> archivedMessages;
        try {
            for (Map<String, String> mailboxData : loadDataFromFilename(filePath + MAILBOXES_FILE_NAME)) {
                loadedMailboxes.add(new Mailbox(mailboxData, logger));
            }
            activeMessages = parseMessages(loadDataFromFilename(filePath + ACTIVE_MESSAGES_FILE_NAME), logger, configService);
            archivedMessages = parseMessages(loadDataFromFilename(filePath + ARCHIVED_MESSAGES_FILE_NAME), logger, configService);
        } catch (IOException | RuntimeException e) {
            // RuntimeException covers Gson's syntax errors and a mailbox or message with a
            // missing or malformed field (a UUID or a number that does not parse).
            loadFailed = true;
            logger.logError("Mailboxes and messages could not be loaded from " + filePath + " (" + e + "). The files "
                    + "have been left untouched and will not be overwritten; fix or restore them and restart the server.");
            return false;
        }

        persistentData.getMailboxes().clear();
        for (Mailbox mailbox : loadedMailboxes) {
            persistentData.addMailbox(mailbox);
        }
        assignMessages(activeMessages, archivedMessages);
        loadFailed = false;
        // One "<n> <label> loaded" line per stored type: the release gates' save-compatibility
        // check compares these counts between the stable release and the candidate.
        java.util.logging.Logger info = mailboxes != null
                ? mailboxes.getLogger() : java.util.logging.Logger.getLogger(StorageService.class.getName());
        info.info(loadedMailboxes.size() + " mailboxes loaded");
        info.info(activeMessages.size() + " active messages loaded");
        info.info(archivedMessages.size() + " archived messages loaded");
        return true;
    }

    /**
     * Whether the last {@link #load()} failed (or none has run yet), in which case
     * {@link #save()} is refused.
     */
    public boolean isLoadFailed() {
        return loadFailed;
    }

    private boolean saveMailboxes() {
        List<Map<String, String>> Mailboxes = new ArrayList<>();
        for (Mailbox mailbox : persistentData.getMailboxes()){
            Mailboxes.add(mailbox.save());
        }

        return writeOutFiles(Mailboxes, MAILBOXES_FILE_NAME);
    }

    private boolean saveMessages(String fileName) {
        List<Map<String, String>> messagesData = new ArrayList<>();
        for (Mailbox mailbox : persistentData.getMailboxes()) {
            ArrayList<Message> messages;
            if (fileName.equals(ACTIVE_MESSAGES_FILE_NAME)) {
                messages = mailbox.getActiveMessages();
            }
            else if (fileName.equals(ARCHIVED_MESSAGES_FILE_NAME)) {
                messages = mailbox.getArchivedMessages();
            }
            else {
                logger.log("Error: " + fileName + " not recognized.");
                return false;
            }
            for (Message message : messages){
                if (message instanceof PlayerMessage) {
                    PlayerMessage playerMessage = (PlayerMessage) message;
                    messagesData.add(playerMessage.save());
                }
                else if (message instanceof PluginMessage) {
                    PluginMessage pluginMessage = (PluginMessage) message;
                    messagesData.add(pluginMessage.save());
                }
                else {
                    messagesData.add(message.save());
                }
            }
        }

        return writeOutFiles(messagesData, fileName);
    }

    // Written to a sibling temp file and moved into place, so a server killed or a disk filled
    // mid-write leaves the previous file intact instead of a truncated one.
    private boolean writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(filePath);
            if (!parentFolder.isDirectory() && !parentFolder.mkdirs()) {
                throw new IOException(parentFolder + " could not be created");
            }
            Path target = new File(filePath, fileName).toPath();
            Path temp = target.resolveSibling(fileName + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                gson.toJson(saveData, writer);
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException e) {
            logger.logError("Could not save " + filePath + fileName + ": " + e);
            return false;
        }
    }

    // add messages to the correct mailboxes
    private void assignMessages(List<Message> activeMessages, List<Message> archivedMessages) {
        for (Mailbox mailbox : persistentData.getMailboxes()) {
            mailbox.getActiveMessages().clear();
            mailbox.getArchivedMessages().clear();
            for (Message message : activeMessages) {
                if (message.getMailboxID() == mailbox.getID()) {
                    mailbox.addActiveMessage(message);
                }
            }
            for (Message message : archivedMessages) {
                if (message.getMailboxID() == mailbox.getID()) {
                    mailbox.addArchivedMessage(message);
                }
            }
        }
    }

    static ArrayList<Message> parseMessages(List<? extends Map<String, String>> messagesData, Logger logger, ConfigService configService) {
        ArrayList<Message> messages = new ArrayList<>();
        for (Map<String, String> messageData : messagesData) {
            Message message = new Message(messageData, logger, configService);
            if (message.getType().equalsIgnoreCase("Default Message")) {
                messages.add(message);
            }
            else if (message.getType().equalsIgnoreCase("Player Message")) {
                messages.add(new PlayerMessage(messageData, logger, configService));
            }
            else if (message.getType().equalsIgnoreCase("Plugin Message")) {
                messages.add(new PluginMessage(messageData, logger, configService));
            }
            else {
                logger.log("Unsupported message type encountered: " + message.getType());
            }
        }
        return messages;
    }

    // A file that does not exist yet holds no data. Nor does an empty one: it can only be left by
    // a write cut off before this class wrote through a temp file, and there is nothing in it
    // to keep.
    private List<HashMap<String, String>> loadDataFromFilename(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<HashMap<String, String>> data = gson.fromJson(reader, LIST_MAP_TYPE);
            return data == null ? new ArrayList<>() : data;
        }
    }

}
