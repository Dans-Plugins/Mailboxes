package dansplugins.mailboxes;

import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.PermissionChecker;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommandInterpreterTest {

    private static final String[] SUBCOMMANDS = {
            "help", "config", "list", "send", "open", "delete", "archive", "stats"
    };

    @Mock
    private CommandSender sender;

    @Mock
    private PermissionChecker permissionChecker;

    private CommandInterpreter commandInterpreter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mailboxes is final and so cannot be mocked; it is only read on the no-arguments
        // path, which these tests do not exercise. The remaining collaborators are only
        // handed to command objects that are never reached when permission is denied.
        commandInterpreter = new CommandInterpreter(null, new ArgumentParser(), permissionChecker,
                null, null, null, null, null, null);
    }

    private String getOutput() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(captor.capture());
        List<String> lines = captor.getAllValues();
        return String.join("\n", lines);
    }

    @Test
    public void testEachSubcommandChecksItsOwnPermissionNode() {
        when(permissionChecker.checkPermission(any(), anyString())).thenReturn(false);

        for (String subcommand : SUBCOMMANDS) {
            assertFalse(commandInterpreter.interpretCommand(sender, "m", new String[]{subcommand}));
            verify(permissionChecker).checkPermission(sender, "mailboxes." + subcommand);
        }
        verifyNoMoreInteractions(permissionChecker);
    }

    @Test
    public void testDeniedPermissionDoesNotFallThroughToUnrecognizedCommand() {
        when(permissionChecker.checkPermission(any(), anyString())).thenReturn(false);

        commandInterpreter.interpretCommand(sender, "m", new String[]{"list"});

        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    public void testSubcommandMatchingIsCaseInsensitive() {
        when(permissionChecker.checkPermission(any(), anyString())).thenReturn(false);

        commandInterpreter.interpretCommand(sender, "m", new String[]{"ArChIvE", "3"});

        verify(permissionChecker).checkPermission(sender, "mailboxes.archive");
    }

    @Test
    public void testBothLabelsAreAcceptedCaseInsensitively() {
        when(permissionChecker.checkPermission(any(), anyString())).thenReturn(false);

        commandInterpreter.interpretCommand(sender, "MAILBOXES", new String[]{"help"});
        commandInterpreter.interpretCommand(sender, "M", new String[]{"help"});

        verify(permissionChecker, times(2)).checkPermission(sender, "mailboxes.help");
    }

    @Test
    public void testPermittedHelpRunsHelpCommand() {
        when(permissionChecker.checkPermission(sender, "mailboxes.help")).thenReturn(true);

        assertTrue(commandInterpreter.interpretCommand(sender, "m", new String[]{"help"}));
        assertTrue(getOutput().contains("/m send"));
    }

    @Test
    public void testUnknownSubcommandIsReported() {
        assertFalse(commandInterpreter.interpretCommand(sender, "m", new String[]{"frobnicate"}));

        assertTrue(getOutput().contains("doesn't recognize that command"));
        verifyNoInteractions(permissionChecker);
    }

    @Test
    public void testUnrelatedLabelIsIgnored() {
        assertFalse(commandInterpreter.interpretCommand(sender, "other", new String[]{"help"}));

        verifyNoInteractions(sender);
        verifyNoInteractions(permissionChecker);
    }
}
