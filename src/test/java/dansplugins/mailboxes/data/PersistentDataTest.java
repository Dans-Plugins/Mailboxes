package dansplugins.mailboxes.data;

import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;

public class PersistentDataTest {

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    private PersistentData persistentData;
    private Mailbox mailbox;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistentData = new PersistentData(logger);
        mailbox = new Mailbox(logger, 1, UUID.randomUUID());
        persistentData.addMailbox(mailbox);
    }

    private Message createMessage(int ID) {
        return new Message(logger, configService, ID, "Default Message", "sender", "recipient", "content");
    }

    @Test
    public void testGetMessageFindsActiveMessage() {
        mailbox.addActiveMessage(createMessage(10));

        assertNotNull(persistentData.getMessage(10));
    }

    @Test
    public void testGetMessageDoesNotFindArchivedMessage() {
        mailbox.addArchivedMessage(createMessage(11));

        assertNull(persistentData.getMessage(11));
    }

    @Test
    public void testIsMessageIDInUseWithActiveMessage() {
        mailbox.addActiveMessage(createMessage(20));

        assertTrue(persistentData.isMessageIDInUse(20));
    }

    @Test
    public void testIsMessageIDInUseWithArchivedMessage() {
        mailbox.addArchivedMessage(createMessage(21));

        assertTrue(persistentData.isMessageIDInUse(21));
    }

    @Test
    public void testIsMessageIDInUseWithUnusedID() {
        mailbox.addActiveMessage(createMessage(22));
        mailbox.addArchivedMessage(createMessage(23));

        assertFalse(persistentData.isMessageIDInUse(24));
    }

    @Test
    public void testIsMessageIDInUseSearchesEveryMailbox() {
        Mailbox otherMailbox = new Mailbox(logger, 2, UUID.randomUUID());
        persistentData.addMailbox(otherMailbox);
        otherMailbox.addArchivedMessage(createMessage(30));

        assertTrue(persistentData.isMessageIDInUse(30));
    }

    @Test
    public void testIsMessageIDInUseWithNoMailboxes() {
        PersistentData empty = new PersistentData(logger);

        assertFalse(empty.isMessageIDInUse(1));
    }
}
