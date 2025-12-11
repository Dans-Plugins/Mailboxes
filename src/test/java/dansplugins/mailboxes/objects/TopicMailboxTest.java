package dansplugins.mailboxes.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TopicMailboxTest {

    private TopicMailbox topicMailbox;
    private TopicMessage message1;
    private TopicMessage message2;

    @BeforeEach
    void setUp() {
        topicMailbox = new TopicMailbox(1, "test-topic", "Test topic description");
        message1 = new TopicMessage(1, "test-topic", "Plugin1", "Message 1");
        message2 = new TopicMessage(2, "test-topic", "Plugin2", "Message 2");
    }

    @Test
    void testConstructor() {
        assertEquals(1, topicMailbox.getID());
        assertEquals("test-topic", topicMailbox.getName());
        assertEquals("Test topic description", topicMailbox.getDescription());
        assertTrue(topicMailbox.getMessages().isEmpty());
        assertTrue(topicMailbox.getSubscribedPlugins().isEmpty());
    }

    @Test
    void testSetters() {
        topicMailbox.setID(2);
        assertEquals(2, topicMailbox.getID());

        topicMailbox.setName("new-topic");
        assertEquals("new-topic", topicMailbox.getName());

        topicMailbox.setDescription("New description");
        assertEquals("New description", topicMailbox.getDescription());
    }

    @Test
    void testAddMessage() {
        topicMailbox.addMessage(message1);
        assertEquals(1, topicMailbox.getMessages().size());
        assertTrue(topicMailbox.getMessages().contains(message1));
    }

    @Test
    void testRemoveMessage() {
        topicMailbox.addMessage(message1);
        topicMailbox.addMessage(message2);
        assertEquals(2, topicMailbox.getMessages().size());

        topicMailbox.removeMessage(message1);
        assertEquals(1, topicMailbox.getMessages().size());
        assertFalse(topicMailbox.getMessages().contains(message1));
        assertTrue(topicMailbox.getMessages().contains(message2));
    }

    @Test
    void testGetUnconsumedMessages() {
        topicMailbox.addMessage(message1);
        topicMailbox.addMessage(message2);

        List<TopicMessage> unconsumed = topicMailbox.getUnconsumedMessages();
        assertEquals(2, unconsumed.size());

        message1.setConsumed(true);
        unconsumed = topicMailbox.getUnconsumedMessages();
        assertEquals(1, unconsumed.size());
        assertTrue(unconsumed.contains(message2));
        assertFalse(unconsumed.contains(message1));
    }

    @Test
    void testAddSubscriber() {
        topicMailbox.addSubscriber("Plugin1");
        assertEquals(1, topicMailbox.getSubscribedPlugins().size());
        assertTrue(topicMailbox.getSubscribedPlugins().contains("Plugin1"));
    }

    @Test
    void testAddSubscriberPreventsDuplicates() {
        topicMailbox.addSubscriber("Plugin1");
        topicMailbox.addSubscriber("Plugin1");
        assertEquals(1, topicMailbox.getSubscribedPlugins().size());
    }

    @Test
    void testRemoveSubscriber() {
        topicMailbox.addSubscriber("Plugin1");
        topicMailbox.addSubscriber("Plugin2");
        assertEquals(2, topicMailbox.getSubscribedPlugins().size());

        topicMailbox.removeSubscriber("Plugin1");
        assertEquals(1, topicMailbox.getSubscribedPlugins().size());
        assertFalse(topicMailbox.getSubscribedPlugins().contains("Plugin1"));
        assertTrue(topicMailbox.getSubscribedPlugins().contains("Plugin2"));
    }

    @Test
    void testIsSubscribed() {
        assertFalse(topicMailbox.isSubscribed("Plugin1"));

        topicMailbox.addSubscriber("Plugin1");
        assertTrue(topicMailbox.isSubscribed("Plugin1"));
        assertFalse(topicMailbox.isSubscribed("Plugin2"));
    }

    @Test
    void testSaveAndLoad() {
        topicMailbox.addSubscriber("Plugin1");
        topicMailbox.addSubscriber("Plugin2");

        // Save the topic mailbox
        Map<String, String> savedData = topicMailbox.save();

        // Verify saved data contains expected keys
        assertNotNull(savedData.get("ID"));
        assertNotNull(savedData.get("name"));
        assertNotNull(savedData.get("description"));
        assertNotNull(savedData.get("subscribedPlugins"));

        // Create a new topic mailbox from saved data
        TopicMailbox loadedMailbox = new TopicMailbox(savedData);

        // Verify loaded mailbox matches original
        assertEquals(topicMailbox.getID(), loadedMailbox.getID());
        assertEquals(topicMailbox.getName(), loadedMailbox.getName());
        assertEquals(topicMailbox.getDescription(), loadedMailbox.getDescription());
        assertEquals(topicMailbox.getSubscribedPlugins().size(), loadedMailbox.getSubscribedPlugins().size());
        assertTrue(loadedMailbox.getSubscribedPlugins().contains("Plugin1"));
        assertTrue(loadedMailbox.getSubscribedPlugins().contains("Plugin2"));
    }

    @Test
    void testSaveAndLoadWithEmptySubscribers() {
        Map<String, String> savedData = topicMailbox.save();
        TopicMailbox loadedMailbox = new TopicMailbox(savedData);

        assertEquals(0, loadedMailbox.getSubscribedPlugins().size());
    }
}
