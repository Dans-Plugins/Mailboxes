package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.services.ConfigService;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConfigCommandTest {

    @Mock
    private ConfigService configService;

    @Mock
    private CommandSender sender;

    private ConfigCommand configCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        configCommand = new ConfigCommand(configService);
    }

    @Test
    public void testExecuteWithNoArgs() {
        boolean result = configCommand.execute(sender, new String[]{});

        assertFalse(result);
        verify(sender).sendMessage(contains("Sub-commands: show, set"));
    }

    @Test
    public void testExecuteWithUnknownSubCommand() {
        boolean result = configCommand.execute(sender, new String[]{"reset"});

        assertFalse(result);
        verify(sender).sendMessage(contains("Sub-commands: show, set"));
    }

    @Test
    public void testExecuteShowDelegatesToConfigService() {
        boolean result = configCommand.execute(sender, new String[]{"show"});

        assertTrue(result);
        verify(configService).sendConfigList(sender);
    }

    @Test
    public void testExecuteSetWithTooFewArgsShowsUsageForARegisteredLabel() {
        boolean result = configCommand.execute(sender, new String[]{"set", "debugMode"});

        assertFalse(result);
        // 'mailboxes' and 'm' are the only labels registered in plugin.yml.
        verify(sender).sendMessage(contains("Usage: /m config set (option) (value)"));
        verify(configService, never()).setConfigOption(anyString(), anyString(), any(CommandSender.class));
    }

    @Test
    public void testExecuteSetDelegatesToConfigService() {
        boolean result = configCommand.execute(sender, new String[]{"set", "debugMode", "true"});

        assertTrue(result);
        verify(configService).setConfigOption("debugMode", "true", sender);
    }
}
