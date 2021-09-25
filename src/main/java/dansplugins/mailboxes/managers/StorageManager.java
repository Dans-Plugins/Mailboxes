package dansplugins.mailboxes.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;

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
    private final static String MESSAGES_FILE_NAME = "messages.json";

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
        if (ConfigManager.getInstance().hasBeenAltered()) {
            Mailboxes.getInstance().saveConfig();
        }
    }

    public void load() {
        loadMailboxes();
    }

    private void saveMailboxes() {
        // save each currency object individually
        List<Map<String, String>> Mailboxes = new ArrayList<>();
        for (Mailbox mailbox : PersistentData.getInstance().getMailboxes()){
            Mailboxes.add(mailbox.save());
            saveMessages(mailbox);
        }

        writeOutFiles(Mailboxes, MAILBOXES_FILE_NAME);
    }

    private void saveMessages(Mailbox mailbox) {
        // save each coinpurse object individually
        List<Map<String, String>> messages = new ArrayList<>();
        for (Message message : mailbox.getMessages()){
            messages.add(mailbox.save());
        }

        writeOutFiles(messages, MESSAGES_FILE_NAME);
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
            loadMessages(mailbox);
        }
    }

    private void loadMessages(Mailbox mailbox) {
        mailbox.getMessages().clear();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + MESSAGES_FILE_NAME);

        for (Map<String, String> messageData : data){
            Message message = new Message(messageData);
            mailbox.addMessage(message);
        }
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        }
        return new ArrayList<>();
    }

}