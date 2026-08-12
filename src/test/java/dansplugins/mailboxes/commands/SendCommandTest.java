package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.services.MailService;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SendCommandTest {

    @Mock
    private Logger logger;

    @Mock
    private UUIDChecker uuidChecker;

    @Mock
    private ConfigService configService;

    @Mock
    private ArgumentParser argumentParser;

    @Mock
    private MessageFactory messageFactory;

    @Mock
    private MailService mailService;

    @Mock
    private Player player;

    @Mock
    private CommandSender nonPlayerSender;

    private SendCommand sendCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sendCommand = new SendCommand(logger, uuidChecker, configService, argumentParser, messageFactory, mailService);
    }

    @Test
    public void testExecuteWithNonPlayerSender() {
        boolean result = sendCommand.execute(nonPlayerSender, new String[]{"Notch", "\"hello\""});

        assertFalse(result);
    }

    @Test
    public void testExecuteWithTooFewArgsShowsUsageWithDoubleQuotes() {
        boolean result = sendCommand.execute(player, new String[]{"Notch"});

        assertFalse(result);
        // ArgumentParser.getArgumentsInsideDoubleQuotes only recognizes double quotes,
        // so the usage string has to show double quotes rather than single ones.
        verify(player).sendMessage(contains("Usage: /m send (playerName) \"message\" [-attach]"));
    }

    @Test
    public void testExecuteWithUnknownRecipient() {
        when(uuidChecker.findUUIDBasedOnPlayerName("Notch")).thenReturn(null);

        boolean result = sendCommand.execute(player, new String[]{"Notch", "\"hello\""});

        assertFalse(result);
        verify(player).sendMessage(contains("That player wasn't found."));
    }

    @Test
    public void testExecuteWithoutDoubleQuotedContent() {
        when(uuidChecker.findUUIDBasedOnPlayerName("Notch")).thenReturn(UUID.randomUUID());
        when(argumentParser.getArgumentsInsideDoubleQuotes(any(String[].class))).thenReturn(new ArrayList<String>());

        boolean result = sendCommand.execute(player, new String[]{"Notch", "hello"});

        assertFalse(result);
        verify(player).sendMessage(contains("Message must be designated between double quotes."));
    }
}
