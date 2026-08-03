package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArchiveCommandTest {

    @Mock
    private PersistentData persistentData;

    @Mock
    private Player player;

    @Mock
    private Mailbox mailbox;

    @Mock
    private Message message;

    @Mock
    private CommandSender nonPlayerSender;

    private ArchiveCommand archiveCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        archiveCommand = new ArchiveCommand(persistentData);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
    }

    @Test
    public void testExecuteWithNonPlayerSender() {
        boolean result = archiveCommand.execute(nonPlayerSender, new String[]{});

        assertFalse(result);
    }

    @Test
    public void testExecuteWithNoArgs() {
        boolean result = archiveCommand.execute(player, new String[]{});

        assertFalse(result);
        verify(player).sendMessage(contains("Usage: /m archive (ID)"));
    }

    @Test
    public void testExecuteWithInvalidID() {
        boolean result = archiveCommand.execute(player, new String[]{"abc"});

        assertFalse(result);
        verify(player).sendMessage(contains("Invalid message ID: abc"));
    }

    @Test
    public void testExecuteWithNoMailbox() {
        when(persistentData.getMailbox(player)).thenReturn(null);

        boolean result = archiveCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("Error: Mailbox wasn't found."));
    }

    @Test
    public void testExecuteWithMessageNotFound() {
        when(mailbox.getMessage(1)).thenReturn(null);

        boolean result = archiveCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("That message wasn't found."));
    }

    @Test
    public void testExecuteWithAlreadyArchivedMessage() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.isArchived()).thenReturn(true);

        boolean result = archiveCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("That message is already archived."));
        verify(mailbox, never()).archiveMessage(message);
    }

    @Test
    public void testExecuteWithMessageNotBelongingToMailbox() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.isArchived()).thenReturn(false);
        when(message.getMailboxID()).thenReturn(2);
        when(mailbox.getID()).thenReturn(1);

        boolean result = archiveCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("That message doesn't belong to you."));
        verify(mailbox, never()).archiveMessage(message);
    }

    @Test
    public void testExecuteSuccessfullyArchivesMessage() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.isArchived()).thenReturn(false);
        when(message.getMailboxID()).thenReturn(1);
        when(mailbox.getID()).thenReturn(1);

        boolean result = archiveCommand.execute(player, new String[]{"1"});

        assertTrue(result);
        verify(mailbox).archiveMessage(message);
        verify(player).sendMessage(contains("Archived."));
    }
}
