package dansplugins.mailboxes.commands;

import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HelpCommandTest {

    @Mock
    private CommandSender sender;

    private HelpCommand helpCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        helpCommand = new HelpCommand();
    }

    private String getHelpOutput() {
        helpCommand.execute(sender);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(captor.capture());
        List<String> lines = captor.getAllValues();
        return String.join("\n", lines);
    }

    @Test
    public void testExecuteReturnsTrue() {
        assertTrue(helpCommand.execute(sender));
    }

    @Test
    public void testEveryCommandIsListed() {
        String output = getHelpOutput();

        assertTrue(output.contains("/m help"));
        assertTrue(output.contains("/m list"));
        assertTrue(output.contains("/m open"));
        assertTrue(output.contains("/m send"));
        assertTrue(output.contains("/m delete"));
        assertTrue(output.contains("/m archive"));
        assertTrue(output.contains("/m config"));
        assertTrue(output.contains("/m stats"));
    }

    @Test
    public void testIDRequiringCommandsAreShownWithTheirIDArgument() {
        String output = getHelpOutput();

        // OpenCommand, DeleteCommand and ArchiveCommand all reject a call with no ID,
        // so the help output has to show the ID argument.
        assertTrue(output.contains("/m open (ID)"));
        assertTrue(output.contains("/m delete (ID)"));
        assertTrue(output.contains("/m archive (ID)"));
    }

    @Test
    public void testSendIsShownWithDoubleQuotesAroundTheMessage() {
        String output = getHelpOutput();

        // ArgumentParser.getArgumentsInsideDoubleQuotes only recognizes double quotes.
        assertTrue(output.contains("/m send (player) \"message\""));
        assertFalse(output.contains("'message'"));
    }

    @Test
    public void testListTypesAreDocumented() {
        String output = getHelpOutput();

        assertTrue(output.contains("/m list [type] [page]"));
        assertTrue(output.contains("active"));
        assertTrue(output.contains("archived"));
        assertTrue(output.contains("unread"));
    }

    @Test
    public void testNoCommandIsAdvertisedUnderAnUnregisteredLabel() {
        String output = getHelpOutput();

        // 'mailboxes' and 'm' are the only labels registered in plugin.yml.
        assertFalse(output.contains("/c "));
    }
}
