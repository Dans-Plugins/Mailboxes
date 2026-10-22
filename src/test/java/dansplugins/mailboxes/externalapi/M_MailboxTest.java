package dansplugins.mailboxes.externalapi;

import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class M_MailboxTest {

    @Mock
    private Mailbox mailbox;

    @Mock
    private Message message;

    @Mock
    private Player player;

    private M_Mailbox wrapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        wrapper = new M_Mailbox(mailbox);
    }

    @Test
    public void testGetIDReturnsWrappedMailboxID() {
        when(mailbox.getID()).thenReturn(7);

        assertEquals(7, wrapper.getID());
    }

    @Test
    public void testGetOwnerUUIDReturnsWrappedMailboxOwner() {
        UUID owner = UUID.randomUUID();
        when(mailbox.getOwnerUUID()).thenReturn(owner);

        assertEquals(owner, wrapper.getOwnerUUID());
    }

    @Test
    public void testGetMessageDelegatesWithTheGivenID() {
        when(mailbox.getMessage(3)).thenReturn(message);

        assertSame(message, wrapper.getMessage(3));
    }

    @Test
    public void testGetMessageReturnsNullWhenMailboxHasNoSuchMessage() {
        when(mailbox.getMessage(3)).thenReturn(null);

        assertNull(wrapper.getMessage(3));
    }

    @Test
    public void testGetActiveMessagesReturnsWrappedMailboxList() {
        ArrayList<Message> active = new ArrayList<>();
        active.add(message);
        when(mailbox.getActiveMessages()).thenReturn(active);

        assertSame(active, wrapper.getActiveMessages());
    }

    @Test
    public void testGetActiveMessageDelegatesWithTheGivenID() {
        when(mailbox.getActiveMessage(5)).thenReturn(message);

        assertSame(message, wrapper.getActiveMessage(5));
    }

    @Test
    public void testGetArchivedMessagesReturnsWrappedMailboxList() {
        ArrayList<Message> archived = new ArrayList<>();
        archived.add(message);
        when(mailbox.getArchivedMessages()).thenReturn(archived);

        assertSame(archived, wrapper.getArchivedMessages());
    }

    @Test
    public void testGetArchivedMessageDelegatesWithTheGivenID() {
        when(mailbox.getArchivedMessage(9)).thenReturn(message);

        assertSame(message, wrapper.getArchivedMessage(9));
    }

    @Test
    public void testSendListOfActiveMessagesToPlayerUsesTheUnpagedOverload() {
        wrapper.sendListOfActiveMessagesToPlayer(player);

        verify(mailbox).sendListOfActiveMessagesToPlayer(player);
        verify(mailbox, never()).sendListOfActiveMessagesToPlayer(any(Player.class), anyInt(), anyInt());
    }

    @Test
    public void testSendListOfArchivedMessagesToPlayerUsesTheUnpagedOverload() {
        wrapper.sendListOfArchivedMessagesToPlayer(player);

        verify(mailbox).sendListOfArchivedMessagesToPlayer(player);
        verify(mailbox, never()).sendListOfArchivedMessagesToPlayer(any(Player.class), anyInt(), anyInt());
    }

    @Test
    public void testAddArchivedMessageDelegates() {
        wrapper.addArchivedMessage(message);

        verify(mailbox).addArchivedMessage(message);
    }

    @Test
    public void testRemoveArchivedMessageByMessageDelegates() {
        wrapper.removeArchivedMessage(message);

        verify(mailbox).removeArchivedMessage(message);
        verify(mailbox, never()).removeArchivedMessage(anyInt());
    }

    @Test
    public void testRemoveArchivedMessageByIDDelegates() {
        wrapper.removeArchivedMessage(4);

        verify(mailbox).removeArchivedMessage(4);
        verify(mailbox, never()).removeArchivedMessage(any(Message.class));
    }

    @Test
    public void testAddActiveMessageDelegates() {
        wrapper.addActiveMessage(message);

        verify(mailbox).addActiveMessage(message);
    }

    @Test
    public void testRemoveActiveMessageByMessageDelegates() {
        wrapper.removeActiveMessage(message);

        verify(mailbox).removeActiveMessage(message);
        verify(mailbox, never()).removeActiveMessage(anyInt());
    }

    @Test
    public void testRemoveActiveMessageByIDDelegates() {
        wrapper.removeActiveMessage(6);

        verify(mailbox).removeActiveMessage(6);
        verify(mailbox, never()).removeActiveMessage(any(Message.class));
    }

    @Test
    public void testRemoveMessageDelegates() {
        wrapper.removeMessage(message);

        verify(mailbox).removeMessage(message);
    }

    @Test
    public void testArchiveMessageDelegates() {
        wrapper.archiveMessage(message);

        verify(mailbox).archiveMessage(message);
    }
}
