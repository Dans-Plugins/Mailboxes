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

public class DeleteCommandTest {

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

    private DeleteCommand deleteCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        deleteCommand = new DeleteCommand(persistentData);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
    }

    @Test
    public void testExecuteWithNonPlayerSender() {
        boolean result = deleteCommand.execute(nonPlayerSender, new String[]{});

        assertFalse(result);
    }

    @Test
    public void testExecuteWithNoArgs() {
        boolean result = deleteCommand.execute(player, new String[]{});

        assertFalse(result);
        verify(player).sendMessage(contains("Usage: /m delete (ID)"));
    }

    @Test
    public void testExecuteWithInvalidID() {
        boolean result = deleteCommand.execute(player, new String[]{"abc"});

        assertFalse(result);
        verify(player).sendMessage(contains("Invalid message ID: abc"));
    }

    @Test
    public void testExecuteWithNoMailbox() {
        when(persistentData.getMailbox(player)).thenReturn(null);

        boolean result = deleteCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("Error: Mailbox wasn't found."));
    }

    @Test
    public void testExecuteWithMessageNotFound() {
        when(mailbox.getMessage(1)).thenReturn(null);

        boolean result = deleteCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("That message wasn't found."));
    }

    @Test
    public void testExecuteWithMessageNotBelongingToMailbox() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.getMailboxID()).thenReturn(2);
        when(mailbox.getID()).thenReturn(1);

        boolean result = deleteCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("That message doesn't belong to you."));
        verify(mailbox, never()).removeMessage(message);
    }

    @Test
    public void testExecuteSuccessfullyDeletesMessage() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.getMailboxID()).thenReturn(1);
        when(mailbox.getID()).thenReturn(1);

        boolean result = deleteCommand.execute(player, new String[]{"1"});

        assertTrue(result);
        verify(mailbox).removeMessage(message);
        verify(player).sendMessage(contains("Deleted."));
    }
}
