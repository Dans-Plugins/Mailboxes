package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MessageFactoryTest {
    // The ID space is deliberately tiny so that a factory which ignores archived IDs
    // is overwhelmingly likely to reissue the archived one within ATTEMPTS draws.
    private static final int ID_SPACE = 2;
    private static final int ARCHIVED_ID = 0;
    private static final int ATTEMPTS = 50;

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    @Mock
    private UUIDChecker uuidChecker;

    private PersistentData persistentData;
    private MessageFactory messageFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistentData = new PersistentData(logger);
        messageFactory = new MessageFactory(uuidChecker, configService, persistentData, logger);
        when(configService.getInt("maxMessageIDNumber")).thenReturn(ID_SPACE);
    }

    private void giveMailboxAnArchivedMessage() {
        Mailbox mailbox = new Mailbox(logger, 1, UUID.randomUUID());
        persistentData.addMailbox(mailbox);
        mailbox.addArchivedMessage(new Message(logger, configService, ARCHIVED_ID, "Default Message", "sender", "recipient", "content"));
    }

    @Test
    public void testCreateMessageDoesNotReuseArchivedMessageID() {
        giveMailboxAnArchivedMessage();

        for (int i = 0; i < ATTEMPTS; i++) {
            Message message = messageFactory.createMessage("sender", "recipient", "content");
            assertNotEquals("A new message was given the ID of an archived message", ARCHIVED_ID, message.getID());
        }
    }

    @Test
    public void testCreatePlayerMessageDoesNotReuseArchivedMessageID() {
        giveMailboxAnArchivedMessage();

        for (int i = 0; i < ATTEMPTS; i++) {
            PlayerMessage message = messageFactory.createPlayerMessage(UUID.randomUUID(), UUID.randomUUID(), "content");
            assertNotEquals("A new player message was given the ID of an archived message", ARCHIVED_ID, message.getID());
        }
    }

    @Test
    public void testCreatePluginMessageDoesNotReuseArchivedMessageID() {
        giveMailboxAnArchivedMessage();

        for (int i = 0; i < ATTEMPTS; i++) {
            PluginMessage message = messageFactory.createPluginMessage("SomePlugin", UUID.randomUUID(), "content");
            assertNotEquals("A new plugin message was given the ID of an archived message", ARCHIVED_ID, message.getID());
        }
    }

    @Test
    public void testCreateMessageDoesNotReuseActiveMessageID() {
        Mailbox mailbox = new Mailbox(logger, 1, UUID.randomUUID());
        persistentData.addMailbox(mailbox);
        mailbox.addActiveMessage(new Message(logger, configService, ARCHIVED_ID, "Default Message", "sender", "recipient", "content"));

        for (int i = 0; i < ATTEMPTS; i++) {
            Message message = messageFactory.createMessage("sender", "recipient", "content");
            assertNotEquals("A new message was given the ID of an active message", ARCHIVED_ID, message.getID());
        }
    }

    @Test
    public void testCreatedMessageCarriesTheRequestedFields() {
        Message message = messageFactory.createMessage("sender", "recipient", "content");

        assertEquals("sender", message.getSender());
        assertEquals("recipient", message.getRecipient());
        assertEquals("content", message.getContent());
    }
}
