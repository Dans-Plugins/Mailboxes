package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StatsCommandTest {

    @Mock
    private Logger logger;

    @Mock
    private PersistentData persistentData;

    @Mock
    private Player player;

    @Mock
    private Mailbox mailbox;

    @Mock
    private CommandSender nonPlayerSender;

    private StatsCommand statsCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        statsCommand = new StatsCommand(logger, persistentData);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
    }

    @Test
    public void testExecuteWithNonPlayerSender() {
        // When a non-player tries to execute the command
        boolean result = statsCommand.execute(nonPlayerSender);

        // Then it should fail and log a message
        assertFalse(result);
        verify(logger).log("Only players can use this command.");
    }

    @Test
    public void testExecuteWithNoMailbox() {
        // Given a player with no mailbox
        when(persistentData.getMailbox(player)).thenReturn(null);

        // When the command is executed
        boolean result = statsCommand.execute(player);

        // Then it should fail with error message
        assertFalse(result);
        verify(player).sendMessage(contains("ERROR: Mailbox was not found."));
    }

    @Test
    public void testExecuteReportsMessageCounts() {
        // Given a mailbox with active, archived, and unread messages
        Message unreadActive = mock(Message.class);
        when(unreadActive.isUnread()).thenReturn(true);
        Message readActive = mock(Message.class);
        when(readActive.isUnread()).thenReturn(false);
        Message unreadArchived = mock(Message.class);
        when(unreadArchived.isUnread()).thenReturn(true);

        ArrayList<Message> activeMessages = new ArrayList<>();
        activeMessages.add(unreadActive);
        activeMessages.add(readActive);

        ArrayList<Message> archivedMessages = new ArrayList<>();
        archivedMessages.add(unreadArchived);

        ArrayList<Message> unreadMessages = new ArrayList<>();
        unreadMessages.add(unreadActive);
        unreadMessages.add(unreadArchived);

        when(mailbox.getActiveMessages()).thenReturn(activeMessages);
        when(mailbox.getArchivedMessages()).thenReturn(archivedMessages);
        when(mailbox.getUnreadMessages()).thenReturn(unreadMessages);

        // When the command is executed
        boolean result = statsCommand.execute(player);

        // Then it should report the correct counts
        assertTrue(result);
        verify(player).sendMessage(contains("Total messages: 3"));
        verify(player).sendMessage(contains("Active messages: 2"));
        verify(player).sendMessage(contains("Archived messages: 1"));
        verify(player).sendMessage(contains("Unread messages: 2"));
    }

    @Test
    public void testExecuteWithEmptyMailbox() {
        // Given a mailbox with no messages
        when(mailbox.getActiveMessages()).thenReturn(new ArrayList<>());
        when(mailbox.getArchivedMessages()).thenReturn(new ArrayList<>());
        when(mailbox.getUnreadMessages()).thenReturn(new ArrayList<>());

        // When the command is executed
        boolean result = statsCommand.execute(player);

        // Then it should report zero counts
        assertTrue(result);
        verify(player).sendMessage(contains("Total messages: 0"));
        verify(player).sendMessage(contains("Active messages: 0"));
        verify(player).sendMessage(contains("Archived messages: 0"));
        verify(player).sendMessage(contains("Unread messages: 0"));
    }
}
