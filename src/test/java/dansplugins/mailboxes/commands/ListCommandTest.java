package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ListCommandTest {

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

    private ListCommand listCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        listCommand = new ListCommand(logger, persistentData);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
    }

    @Test
    public void testExecuteWithNonPlayerSender() {
        // When a non-player tries to execute the command
        boolean result = listCommand.execute(nonPlayerSender, new String[]{});

        // Then it should fail and log a message
        assertFalse(result);
        verify(logger).log("Only players can use this command");
    }

    @Test
    public void testExecuteWithNoMailbox() {
        // Given a player with no mailbox
        when(persistentData.getMailbox(player)).thenReturn(null);

        // When the command is executed
        boolean result = listCommand.execute(player, new String[]{});

        // Then it should fail with error message
        assertFalse(result);
        verify(player).sendMessage(contains("ERROR: Mailbox was not found."));
    }

    @Test
    public void testExecuteWithNoArgsDefaultsToActivePage1() {
        // When executing with no arguments
        boolean result = listCommand.execute(player, new String[]{});

        // Then it should show active messages page 1
        assertTrue(result);
        verify(mailbox).sendListOfActiveMessagesToPlayer(player, 1, 10);
    }

    @Test
    public void testExecuteWithActiveListType() {
        // When executing with "active" argument
        boolean result = listCommand.execute(player, new String[]{"active"});

        // Then it should show active messages page 1
        assertTrue(result);
        verify(mailbox).sendListOfActiveMessagesToPlayer(player, 1, 10);
    }

    @Test
    public void testExecuteWithArchivedListType() {
        // When executing with "archived" argument
        boolean result = listCommand.execute(player, new String[]{"archived"});

        // Then it should show archived messages page 1
        assertTrue(result);
        verify(mailbox).sendListOfArchivedMessagesToPlayer(player, 1, 10);
    }

    @Test
    public void testExecuteWithUnreadListType() {
        // When executing with "unread" argument
        boolean result = listCommand.execute(player, new String[]{"unread"});

        // Then it should show unread messages page 1
        assertTrue(result);
        verify(mailbox).sendListOfUnreadMessagesToPlayer(player, 1, 10);
    }

    @Test
    public void testExecuteWithPageNumber() {
        // When executing with page number
        boolean result = listCommand.execute(player, new String[]{"active", "2"});

        // Then it should show active messages page 2
        assertTrue(result);
        verify(mailbox).sendListOfActiveMessagesToPlayer(player, 2, 10);
    }

    @Test
    public void testExecuteWithInvalidPageNumber() {
        // When executing with invalid page number
        boolean result = listCommand.execute(player, new String[]{"active", "abc"});

        // Then it should fail with error message
        assertFalse(result);
        verify(player).sendMessage(contains("Invalid page number: abc"));
    }

    @Test
    public void testExecuteWithNegativePageNumber() {
        // When executing with negative page number
        boolean result = listCommand.execute(player, new String[]{"active", "0"});

        // Then it should fail with error message
        assertFalse(result);
        verify(player).sendMessage(contains("Page number must be 1 or greater."));
    }

    @Test
    public void testExecuteWithInvalidListType() {
        // When executing with invalid list type
        boolean result = listCommand.execute(player, new String[]{"invalid"});

        // Then it should fail with error message
        assertFalse(result);
        verify(player).sendMessage(contains("Sub-commands: active, archived, unread"));
        verify(player).sendMessage(contains("Usage: /m list [type] [page]"));
    }

    @Test
    public void testGetTabCompletionsForListType() {
        // When getting tab completions for second argument
        List<String> completions = listCommand.getTabCompletions(new String[]{"list", "ar"});

        // Then it should return matching list types
        assertNotNull(completions);
        assertEquals(1, completions.size());
        assertTrue(completions.contains("archived"));
    }

    @Test
    public void testGetTabCompletionsReturnsAllTypesOnEmpty() {
        // When getting tab completions with empty input
        List<String> completions = listCommand.getTabCompletions(new String[]{"list", ""});

        // Then it should return all list types
        assertNotNull(completions);
        assertEquals(3, completions.size());
        assertTrue(completions.contains("active"));
        assertTrue(completions.contains("archived"));
        assertTrue(completions.contains("unread"));
    }

    @Test
    public void testGetTabCompletionsForOtherArguments() {
        // When getting tab completions for third argument
        List<String> completions = listCommand.getTabCompletions(new String[]{"list", "active", "1"});

        // Then it should return empty list
        assertNotNull(completions);
        assertTrue(completions.isEmpty());
    }
}
