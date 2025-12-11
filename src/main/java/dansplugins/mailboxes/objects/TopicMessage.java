package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TopicMessage implements Savable {
    private int ID;
    private String topic;
    private String producerPlugin;
    private String content;
    private Date timestamp;
    private boolean consumed;

    public TopicMessage(int ID, String topic, String producerPlugin, String content) {
        this.ID = ID;
        this.topic = topic;
        this.producerPlugin = producerPlugin;
        this.content = content;
        this.timestamp = new Date();
        this.consumed = false;
    }

    public TopicMessage(Map<String, String> data) {
        this.load(data);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProducerPlugin() {
        return producerPlugin;
    }

    public void setProducerPlugin(String producerPlugin) {
        this.producerPlugin = producerPlugin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("topic", gson.toJson(topic));
        saveMap.put("producerPlugin", gson.toJson(producerPlugin));
        saveMap.put("content", gson.toJson(content));
        saveMap.put("timestamp", gson.toJson(timestamp));
        saveMap.put("consumed", gson.toJson(consumed));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        topic = gson.fromJson(data.get("topic"), String.class);
        producerPlugin = gson.fromJson(data.get("producerPlugin"), String.class);
        content = gson.fromJson(data.get("content"), String.class);
        timestamp = gson.fromJson(data.get("timestamp"), Date.class);
        consumed = Boolean.parseBoolean(gson.fromJson(data.get("consumed"), String.class));
    }
}
