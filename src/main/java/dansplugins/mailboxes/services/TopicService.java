package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.TopicMailbox;
import dansplugins.mailboxes.objects.TopicMessage;
import dansplugins.mailboxes.utils.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopicService {
    private final PersistentData persistentData;
    private final Logger logger;
    private final AtomicInteger nextTopicMailboxId = new AtomicInteger(1);
    private final AtomicInteger nextTopicMessageId = new AtomicInteger(1);

    public TopicService(PersistentData persistentData, Logger logger) {
        this.persistentData = persistentData;
        this.logger = logger;
    }

    public TopicMailbox createTopic(String name, String description) {
        logger.log("Creating topic: " + name);
        
        if (persistentData.getTopicMailbox(name) != null) {
            logger.log("Topic already exists: " + name);
            return null;
        }

        TopicMailbox topicMailbox = new TopicMailbox(nextTopicMailboxId.getAndIncrement(), name, description);
        persistentData.addTopicMailbox(topicMailbox);
        logger.log("Topic created: " + name);
        return topicMailbox;
    }

    public boolean deleteTopic(String name) {
        logger.log("Deleting topic: " + name);
        TopicMailbox topicMailbox = persistentData.getTopicMailbox(name);
        
        if (topicMailbox == null) {
            logger.log("Topic not found: " + name);
            return false;
        }

        persistentData.removeTopicMailbox(topicMailbox);
        logger.log("Topic deleted: " + name);
        return true;
    }

    public TopicMailbox getTopic(String name) {
        return persistentData.getTopicMailbox(name);
    }

    public List<TopicMailbox> getAllTopics() {
        return persistentData.getTopicMailboxes();
    }

    public TopicMessage publishMessage(String topicName, String producerPlugin, String content) {
        logger.log("Publishing message to topic: " + topicName + " from plugin: " + producerPlugin);
        
        TopicMailbox topicMailbox = persistentData.getTopicMailbox(topicName);
        if (topicMailbox == null) {
            logger.log("Topic not found: " + topicName);
            return null;
        }

        TopicMessage message = new TopicMessage(
            nextTopicMessageId.getAndIncrement(),
            topicName,
            producerPlugin,
            content
        );

        persistentData.addTopicMessage(message);
        topicMailbox.addMessage(message);
        logger.log("Message published to topic: " + topicName + " with ID: " + message.getID());
        return message;
    }

    public List<TopicMessage> consumeMessages(String topicName, String consumerPlugin) {
        logger.log("Consuming messages from topic: " + topicName + " by plugin: " + consumerPlugin);
        
        TopicMailbox topicMailbox = persistentData.getTopicMailbox(topicName);
        if (topicMailbox == null) {
            logger.log("Topic not found: " + topicName);
            return null;
        }

        List<TopicMessage> unconsumedMessages = topicMailbox.getUnconsumedMessages();
        
        // Mark messages as consumed
        for (TopicMessage message : unconsumedMessages) {
            message.setConsumed(true);
        }

        logger.log("Consumed " + unconsumedMessages.size() + " messages from topic: " + topicName);
        return unconsumedMessages;
    }

    public boolean subscribe(String topicName, String pluginName) {
        logger.log("Plugin " + pluginName + " subscribing to topic: " + topicName);
        
        TopicMailbox topicMailbox = persistentData.getTopicMailbox(topicName);
        if (topicMailbox == null) {
            logger.log("Topic not found: " + topicName);
            return false;
        }

        topicMailbox.addSubscriber(pluginName);
        logger.log("Plugin " + pluginName + " subscribed to topic: " + topicName);
        return true;
    }

    public boolean unsubscribe(String topicName, String pluginName) {
        logger.log("Plugin " + pluginName + " unsubscribing from topic: " + topicName);
        
        TopicMailbox topicMailbox = persistentData.getTopicMailbox(topicName);
        if (topicMailbox == null) {
            logger.log("Topic not found: " + topicName);
            return false;
        }

        topicMailbox.removeSubscriber(pluginName);
        logger.log("Plugin " + pluginName + " unsubscribed from topic: " + topicName);
        return true;
    }
}
