package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TopicMailbox implements Savable {
    private int ID;
    private String name;
    private String description;
    private ArrayList<TopicMessage> messages = new ArrayList<>();
    private ArrayList<String> subscribedPlugins = new ArrayList<>();

    public TopicMailbox(int ID, String name, String description) {
        this.ID = ID;
        this.name = name;
        this.description = description;
    }

    public TopicMailbox(Map<String, String> data) {
        this.load(data);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<TopicMessage> getMessages() {
        return messages;
    }

    public void addMessage(TopicMessage message) {
        messages.add(message);
    }

    public void removeMessage(TopicMessage message) {
        messages.remove(message);
    }

    public List<TopicMessage> getUnconsumedMessages() {
        return messages.stream()
                .filter(m -> !m.isConsumed())
                .collect(Collectors.toList());
    }

    public ArrayList<String> getSubscribedPlugins() {
        return subscribedPlugins;
    }

    public void addSubscriber(String pluginName) {
        if (!subscribedPlugins.contains(pluginName)) {
            subscribedPlugins.add(pluginName);
        }
    }

    public void removeSubscriber(String pluginName) {
        subscribedPlugins.remove(pluginName);
    }

    public boolean isSubscribed(String pluginName) {
        return subscribedPlugins.contains(pluginName);
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("name", gson.toJson(name));
        saveMap.put("description", gson.toJson(description));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        name = gson.fromJson(data.get("name"), String.class);
        description = gson.fromJson(data.get("description"), String.class);
    }
}
