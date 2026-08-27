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

import java.util.Random;
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

    /**
     * A factory whose random draws always land on ID 0 — taken in every setup below — so that the
     * behaviour once random draws are exhausted can be exercised deterministically.
     */
    private MessageFactory messageFactoryAlwaysDrawingTakenID() {
        Random alwaysDrawsZero = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        return new MessageFactory(uuidChecker, configService, persistentData, logger, alwaysDrawsZero);
    }

    private void fillIDSpaceExceptFor(int freeID, int idSpace) {
        Mailbox mailbox = new Mailbox(logger, 1, UUID.randomUUID());
        persistentData.addMailbox(mailbox);
        for (int ID = 0; ID < idSpace; ID++) {
            if (ID != freeID) {
                mailbox.addActiveMessage(new Message(logger, configService, ID, "Default Message", "sender", "recipient", "content"));
            }
        }
    }

    @Test
    public void testCreateMessageFallsBackToAFreeIDWhenRandomDrawsKeepColliding() {
        int idSpace = 3;
        int freeID = 2;
        when(configService.getInt("maxMessageIDNumber")).thenReturn(idSpace);
        fillIDSpaceExceptFor(freeID, idSpace);

        Message message = messageFactoryAlwaysDrawingTakenID().createMessage("sender", "recipient", "content");

        assertNotNull("A free ID was available, so a message should have been created", message);
        assertEquals("The one free ID should have been used instead of the repeatedly drawn taken one", freeID, message.getID());
    }

    @Test
    public void testCreatePlayerMessageFallsBackToAFreeIDWhenRandomDrawsKeepColliding() {
        int idSpace = 3;
        int freeID = 2;
        when(configService.getInt("maxMessageIDNumber")).thenReturn(idSpace);
        fillIDSpaceExceptFor(freeID, idSpace);

        PlayerMessage message = messageFactoryAlwaysDrawingTakenID().createPlayerMessage(UUID.randomUUID(), UUID.randomUUID(), "content");

        assertNotNull(message);
        assertEquals(freeID, message.getID());
    }

    @Test
    public void testCreatePluginMessageFallsBackToAFreeIDWhenRandomDrawsKeepColliding() {
        int idSpace = 3;
        int freeID = 2;
        when(configService.getInt("maxMessageIDNumber")).thenReturn(idSpace);
        fillIDSpaceExceptFor(freeID, idSpace);

        PluginMessage message = messageFactoryAlwaysDrawingTakenID().createPluginMessage("SomePlugin", UUID.randomUUID(), "content");

        assertNotNull(message);
        assertEquals(freeID, message.getID());
    }

    @Test
    public void testCreateMessageReturnsNullWhenEveryIDIsTaken() {
        fillIDSpaceExceptFor(-1, ID_SPACE);

        assertNull("A saturated ID space should refuse creation rather than reuse an ID",
                messageFactory.createMessage("sender", "recipient", "content"));
        verify(logger).logError(contains("in use"));
    }

    @Test
    public void testCreatePlayerMessageReturnsNullWhenEveryIDIsTaken() {
        fillIDSpaceExceptFor(-1, ID_SPACE);

        assertNull(messageFactory.createPlayerMessage(UUID.randomUUID(), UUID.randomUUID(), "content"));
    }

    @Test
    public void testCreatePluginMessageReturnsNullWhenEveryIDIsTaken() {
        fillIDSpaceExceptFor(-1, ID_SPACE);

        assertNull(messageFactory.createPluginMessage("SomePlugin", UUID.randomUUID(), "content"));
    }

    @Test
    public void testCreatedMessageCarriesTheRequestedFields() {
        Message message = messageFactory.createMessage("sender", "recipient", "content");

        assertEquals("sender", message.getSender());
        assertEquals("recipient", message.getRecipient());
        assertEquals("content", message.getContent());
    }
}
