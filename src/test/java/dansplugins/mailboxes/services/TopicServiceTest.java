package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.TopicMailbox;
import dansplugins.mailboxes.objects.TopicMessage;
import dansplugins.mailboxes.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private PersistentData persistentData;

    @Mock
    private Logger logger;

    private TopicService topicService;

    @BeforeEach
    void setUp() {
        when(persistentData.getTopicMailboxes()).thenReturn(new java.util.ArrayList<>());
        when(persistentData.getTopicMessages()).thenReturn(new java.util.ArrayList<>());
        topicService = new TopicService(persistentData, logger);
    }

    @Test
    void testCreateTopic() {
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(null);

        TopicMailbox topic = topicService.createTopic("test-topic", "Test description");

        assertNotNull(topic);
        assertEquals("test-topic", topic.getName());
        assertEquals("Test description", topic.getDescription());
        verify(persistentData).addTopicMailbox(topic);
    }

    @Test
    void testCreateTopicThatAlreadyExists() {
        TopicMailbox existingTopic = new TopicMailbox(1, "test-topic", "Existing");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(existingTopic);

        TopicMailbox topic = topicService.createTopic("test-topic", "Test description");

        assertNull(topic);
        verify(persistentData, never()).addTopicMailbox(any());
    }

    @Test
    void testDeleteTopic() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        boolean result = topicService.deleteTopic("test-topic");

        assertTrue(result);
        verify(persistentData).removeTopicMailbox(topic);
    }

    @Test
    void testDeleteNonExistentTopic() {
        when(persistentData.getTopicMailbox("non-existent")).thenReturn(null);

        boolean result = topicService.deleteTopic("non-existent");

        assertFalse(result);
        verify(persistentData, never()).removeTopicMailbox(any());
    }

    @Test
    void testGetTopic() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        TopicMailbox result = topicService.getTopic("test-topic");

        assertEquals(topic, result);
    }

    @Test
    void testGetAllTopics() {
        ArrayList<TopicMailbox> topics = new ArrayList<>();
        topics.add(new TopicMailbox(1, "topic1", "Desc1"));
        topics.add(new TopicMailbox(2, "topic2", "Desc2"));
        when(persistentData.getTopicMailboxes()).thenReturn(topics);

        List<TopicMailbox> result = topicService.getAllTopics();

        assertEquals(2, result.size());
    }

    @Test
    void testPublishMessage() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        TopicMessage message = topicService.publishMessage("test-topic", "ProducerPlugin", "Test content");

        assertNotNull(message);
        assertEquals("test-topic", message.getTopic());
        assertEquals("ProducerPlugin", message.getProducerPlugin());
        assertEquals("Test content", message.getContent());
        assertFalse(message.isConsumed());
        verify(persistentData).addTopicMessage(message);
        assertEquals(1, topic.getMessages().size());
    }

    @Test
    void testPublishMessageToNonExistentTopic() {
        when(persistentData.getTopicMailbox("non-existent")).thenReturn(null);

        TopicMessage message = topicService.publishMessage("non-existent", "ProducerPlugin", "Test content");

        assertNull(message);
        verify(persistentData, never()).addTopicMessage(any());
    }

    @Test
    void testConsumeMessages() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        TopicMessage msg1 = new TopicMessage(1, "test-topic", "Producer1", "Content1");
        TopicMessage msg2 = new TopicMessage(2, "test-topic", "Producer2", "Content2");
        topic.addMessage(msg1);
        topic.addMessage(msg2);
        
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        List<TopicMessage> messages = topicService.consumeMessages("test-topic", "ConsumerPlugin");

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertTrue(msg1.isConsumed());
        assertTrue(msg2.isConsumed());
    }

    @Test
    void testConsumeMessagesOnlyReturnsUnconsumed() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        TopicMessage msg1 = new TopicMessage(1, "test-topic", "Producer1", "Content1");
        TopicMessage msg2 = new TopicMessage(2, "test-topic", "Producer2", "Content2");
        msg1.setConsumed(true); // Already consumed
        topic.addMessage(msg1);
        topic.addMessage(msg2);
        
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        List<TopicMessage> messages = topicService.consumeMessages("test-topic", "ConsumerPlugin");

        assertEquals(1, messages.size());
        assertEquals(msg2, messages.get(0));
        assertTrue(msg2.isConsumed());
    }

    @Test
    void testConsumeMessagesFromNonExistentTopic() {
        when(persistentData.getTopicMailbox("non-existent")).thenReturn(null);

        List<TopicMessage> messages = topicService.consumeMessages("non-existent", "ConsumerPlugin");

        assertNull(messages);
    }

    @Test
    void testSubscribe() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        boolean result = topicService.subscribe("test-topic", "TestPlugin");

        assertTrue(result);
        assertTrue(topic.isSubscribed("TestPlugin"));
    }

    @Test
    void testSubscribeToNonExistentTopic() {
        when(persistentData.getTopicMailbox("non-existent")).thenReturn(null);

        boolean result = topicService.subscribe("non-existent", "TestPlugin");

        assertFalse(result);
    }

    @Test
    void testUnsubscribe() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        topic.addSubscriber("TestPlugin");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        boolean result = topicService.unsubscribe("test-topic", "TestPlugin");

        assertTrue(result);
        assertFalse(topic.isSubscribed("TestPlugin"));
    }

    @Test
    void testUnsubscribeFromNonExistentTopic() {
        when(persistentData.getTopicMailbox("non-existent")).thenReturn(null);

        boolean result = topicService.unsubscribe("non-existent", "TestPlugin");

        assertFalse(result);
    }

    @Test
    void testIdCounterInitializationWithExistingTopics() {
        ArrayList<TopicMailbox> existingTopics = new ArrayList<>();
        existingTopics.add(new TopicMailbox(5, "topic1", "Desc1"));
        existingTopics.add(new TopicMailbox(3, "topic2", "Desc2"));
        
        when(persistentData.getTopicMailboxes()).thenReturn(existingTopics);
        when(persistentData.getTopicMessages()).thenReturn(new ArrayList<>());
        when(persistentData.getTopicMailbox("new-topic")).thenReturn(null);

        TopicService service = new TopicService(persistentData, logger);
        TopicMailbox newTopic = service.createTopic("new-topic", "New");

        // New topic should have ID 6 (max existing ID + 1)
        assertEquals(6, newTopic.getID());
    }

    @Test
    void testMessageIdCounterInitializationWithExistingMessages() {
        ArrayList<TopicMessage> existingMessages = new ArrayList<>();
        existingMessages.add(new TopicMessage(10, "topic1", "Plugin1", "Content1"));
        existingMessages.add(new TopicMessage(7, "topic2", "Plugin2", "Content2"));
        
        when(persistentData.getTopicMailboxes()).thenReturn(new ArrayList<>());
        when(persistentData.getTopicMessages()).thenReturn(existingMessages);

        TopicService service = new TopicService(persistentData, logger);
        
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test");
        when(persistentData.getTopicMailbox("test-topic")).thenReturn(topic);

        TopicMessage newMessage = service.publishMessage("test-topic", "Producer", "Content");

        // New message should have ID 11 (max existing ID + 1)
        assertEquals(11, newMessage.getID());
    }
}
