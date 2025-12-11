package dansplugins.mailboxes.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TopicMessageTest {

    private TopicMessage topicMessage;

    @BeforeEach
    void setUp() {
        topicMessage = new TopicMessage(1, "test-topic", "TestPlugin", "Test content");
    }

    @Test
    void testConstructor() {
        assertEquals(1, topicMessage.getID());
        assertEquals("test-topic", topicMessage.getTopic());
        assertEquals("TestPlugin", topicMessage.getProducerPlugin());
        assertEquals("Test content", topicMessage.getContent());
        assertNotNull(topicMessage.getTimestamp());
        assertFalse(topicMessage.isConsumed());
    }

    @Test
    void testSetters() {
        topicMessage.setID(2);
        assertEquals(2, topicMessage.getID());

        topicMessage.setTopic("new-topic");
        assertEquals("new-topic", topicMessage.getTopic());

        topicMessage.setProducerPlugin("NewPlugin");
        assertEquals("NewPlugin", topicMessage.getProducerPlugin());

        topicMessage.setContent("New content");
        assertEquals("New content", topicMessage.getContent());

        Date newDate = new Date();
        topicMessage.setTimestamp(newDate);
        assertEquals(newDate, topicMessage.getTimestamp());

        topicMessage.setConsumed(true);
        assertTrue(topicMessage.isConsumed());
    }

    @Test
    void testSaveAndLoad() {
        // Save the message
        Map<String, String> savedData = topicMessage.save();

        // Verify saved data contains expected keys
        assertNotNull(savedData.get("ID"));
        assertNotNull(savedData.get("topic"));
        assertNotNull(savedData.get("producerPlugin"));
        assertNotNull(savedData.get("content"));
        assertNotNull(savedData.get("timestamp"));
        assertNotNull(savedData.get("consumed"));

        // Create a new message from saved data
        TopicMessage loadedMessage = new TopicMessage(savedData);

        // Verify loaded message matches original
        assertEquals(topicMessage.getID(), loadedMessage.getID());
        assertEquals(topicMessage.getTopic(), loadedMessage.getTopic());
        assertEquals(topicMessage.getProducerPlugin(), loadedMessage.getProducerPlugin());
        assertEquals(topicMessage.getContent(), loadedMessage.getContent());
        assertEquals(topicMessage.isConsumed(), loadedMessage.isConsumed());
    }

    @Test
    void testSaveAndLoadWithConsumedMessage() {
        topicMessage.setConsumed(true);

        Map<String, String> savedData = topicMessage.save();
        TopicMessage loadedMessage = new TopicMessage(savedData);

        assertTrue(loadedMessage.isConsumed());
    }
}
