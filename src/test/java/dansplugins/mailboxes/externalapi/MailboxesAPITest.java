package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MailboxesAPITest {

    @Mock
    private PersistentData persistentData;

    @Mock
    private Player player;

    @Mock
    private Mailbox mailbox;

    @Mock
    private Message message;

    private MailboxesAPI mailboxesAPI;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mailboxes is final and so cannot be mocked; the lookups under test do not consult it,
        // nor the message factory or mail service.
        mailboxesAPI = new MailboxesAPI(null, persistentData, null, null);
    }

    @Test
    public void testGetMailboxReturnsNullWhenPlayerHasNoMailbox() {
        when(persistentData.getMailbox(player)).thenReturn(null);

        assertNull(mailboxesAPI.getMailbox(player));
    }

    @Test
    public void testGetMailboxWrapsTheFoundMailbox() {
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
        when(mailbox.getID()).thenReturn(42);

        M_Mailbox result = mailboxesAPI.getMailbox(player);

        assertNotNull(result);
        assertEquals(42, result.getID());
    }

    @Test
    public void testGetMessageReturnsNullWhenNoMessageHasTheID() {
        when(persistentData.getMessage(123)).thenReturn(null);

        assertNull(mailboxesAPI.getMessage(123));
    }

    @Test
    public void testGetMessageWrapsTheFoundMessage() {
        when(persistentData.getMessage(123)).thenReturn(message);
        when(message.getContent()).thenReturn("hello");

        M_Message result = mailboxesAPI.getMessage(123);

        assertNotNull(result);
        assertEquals("hello", result.getContent());
    }

    @Test
    public void testGetAPIVersion() {
        assertEquals("v0.0.4", mailboxesAPI.getAPIVersion());
    }
}
