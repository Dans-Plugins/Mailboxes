package dansplugins.mailboxes.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {

    private static StorageManager instance;

    private final static String FILE_PATH = "./plugins/Mailboxes/";
    private final static String MAILBOXES_FILE_NAME = "mailboxes.json";
    private final static String ACTIVE_MESSAGES_FILE_NAME = "activeMessages.json";
    private final static String ARCHIVED_MESSAGES_FILE_NAME = "archivedMessages.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        saveMailboxes();
        saveMessages(ACTIVE_MESSAGES_FILE_NAME);
        saveMessages(ARCHIVED_MESSAGES_FILE_NAME);
        if (ConfigManager.getInstance().hasBeenAltered()) {
            Mailboxes.getInstance().saveConfig();
        }
    }

    public void load() {
        loadMailboxes();
        loadMessages();
    }

    private void saveMailboxes() {
        List<Map<String, String>> Mailboxes = new ArrayList<>();
        for (Mailbox mailbox : PersistentData.getInstance().getMailboxes()){
            Mailboxes.add(mailbox.save());
        }

        writeOutFiles(Mailboxes, MAILBOXES_FILE_NAME);
    }

    private void saveMessages(String fileName) {
        List<Map<String, String>> messagesData = new ArrayList<>();
        for (Mailbox mailbox : PersistentData.getInstance().getMailboxes()) {
            ArrayList<Message> messages;
            if (fileName.equals(ACTIVE_MESSAGES_FILE_NAME)) {
                messages = mailbox.getActiveMessages();
            }
            else if (fileName.equals(ARCHIVED_MESSAGES_FILE_NAME)) {
                messages = mailbox.getArchivedMessages();
            }
            else {
                Logger.getInstance().log("Error: " + fileName + " + not recognized.");
                return;
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

        writeOutFiles(messagesData, fileName);
    }

    private void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH, fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(gson.toJson(saveData));
            outputStreamWriter.close();
        } catch(IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    private void loadMailboxes() {
        PersistentData.getInstance().getMailboxes().clear();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + MAILBOXES_FILE_NAME);

        for (Map<String, String> mailboxData : data){
            Mailbox mailbox = new Mailbox(mailboxData);
            PersistentData.getInstance().addMailbox(mailbox);
        }
    }

    private void loadMessages() {
        ArrayList<HashMap<String, String>> activeMessagesData = loadDataFromFilename(FILE_PATH + ACTIVE_MESSAGES_FILE_NAME);
        ArrayList<HashMap<String, String>> archivedMessagesData = loadDataFromFilename(FILE_PATH + ARCHIVED_MESSAGES_FILE_NAME);

        // load in messages
        ArrayList<Message> activeMessages = new ArrayList<>();
        ArrayList<Message> archivedMessages = new ArrayList<>();
        for (Map<String, String> messageData : activeMessagesData){
            Message message = new Message(messageData);
            if (message.getType().equalsIgnoreCase("Default Message")) {
                activeMessages.add(message);
            }
            else if (message.getType().equalsIgnoreCase("Player Message")) {
                PlayerMessage playerMessage = new PlayerMessage(messageData);
                activeMessages.add(playerMessage);
            }
            else if (message.getType().equalsIgnoreCase("Plugin Message")) {
                PluginMessage pluginMessage = new PluginMessage(messageData);
                activeMessages.add(pluginMessage);
            }
        }
        for (Map<String, String> messageData : archivedMessagesData){
            Message message = new Message(messageData);
            if (message.getType().equalsIgnoreCase("Default Message")) {
                archivedMessages.add(message);
            }
            else if (message.getType().equalsIgnoreCase("Player Message")) {
                PlayerMessage playerMessage = new PlayerMessage(messageData);
                archivedMessages.add(playerMessage);
            }
            else if (message.getType().equalsIgnoreCase("Plugin Message")) {
                PluginMessage pluginMessage = new PluginMessage(messageData);
                archivedMessages.add(pluginMessage);
            }
            else {
                Logger.getInstance().log("Unsupported message type encountered: " + message.getType());
            }
        }

        // add messages to the correct mailboxes
        for (Mailbox mailbox : PersistentData.getInstance().getMailboxes()) {
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

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        }
        return new ArrayList<>();
    }

}