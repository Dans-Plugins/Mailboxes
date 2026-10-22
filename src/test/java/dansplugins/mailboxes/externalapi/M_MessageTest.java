package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class M_MessageTest {

    @Mock
    private Message message;

    @Mock
    private Player player;

    private M_Message wrapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        wrapper = new M_Message(message);
    }

    @Test
    public void testGetIDReturnsWrappedMessageID() {
        when(message.getID()).thenReturn(11);

        assertEquals(11, wrapper.getID());
    }

    @Test
    public void testGetSenderReturnsWrappedMessageSender() {
        when(message.getSender()).thenReturn("Alice");

        assertEquals("Alice", wrapper.getSender());
    }

    @Test
    public void testGetRecipientReturnsWrappedMessageRecipient() {
        when(message.getRecipient()).thenReturn("Bob");

        assertEquals("Bob", wrapper.getRecipient());
    }

    @Test
    public void testGetContentReturnsWrappedMessageContent() {
        when(message.getContent()).thenReturn("hello there");

        assertEquals("hello there", wrapper.getContent());
    }

    @Test
    public void testGetDateReturnsWrappedMessageDate() {
        Date date = new Date(1234567890L);
        when(message.getDate()).thenReturn(date);

        assertSame(date, wrapper.getDate());
    }

    @Test
    public void testGetMailboxIDReturnsWrappedMessageMailboxID() {
        when(message.getMailboxID()).thenReturn(21);

        assertEquals(21, wrapper.getMailboxID());
    }

    @Test
    public void testSendContentToPlayerDelegates() {
        wrapper.sendContentToPlayer(player);

        verify(message).sendContentToPlayer(player);
    }

    @Test
    public void testIsArchivedReflectsWrappedMessage() {
        when(message.isArchived()).thenReturn(true);
        assertTrue(wrapper.isArchived());

        when(message.isArchived()).thenReturn(false);
        assertFalse(wrapper.isArchived());
    }

    @Test
    public void testHasAttachmentsReflectsWrappedMessage() {
        when(message.hasAttachments()).thenReturn(true);
        assertTrue(wrapper.hasAttachments());

        when(message.hasAttachments()).thenReturn(false);
        assertFalse(wrapper.hasAttachments());
    }

    @Test
    public void testGetAttachmentsReturnsWrappedMessageList() {
        List<ItemStack> attachments = new ArrayList<>();
        attachments.add(mock(ItemStack.class));
        when(message.getAttachments()).thenReturn(attachments);

        assertSame(attachments, wrapper.getAttachments());
    }

    @Test
    public void testWrapperDoesNotMutateWrappedMessageOnRead() {
        wrapper.getID();
        wrapper.getSender();
        wrapper.getRecipient();
        wrapper.getContent();
        wrapper.isArchived();
        wrapper.hasAttachments();

        verify(message, never()).setArchived(anyBoolean());
        verify(message, never()).setUnread(anyBoolean());
        verify(message, never()).setContent(anyString());
    }
}
