package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.factories.MessageFactory;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.services.MailService;
import dansplugins.mailboxes.utils.ArgumentParser;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.UUIDChecker;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Mock
    private PlayerMessage message;

    @Mock
    private PlayerInventory inventory;

    @Mock
    private ItemStack itemInHand;

    @Mock
    private ItemStack attachment;

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

    @Test
    public void testExecuteWhenMessageCouldNotBeCreated() {
        when(uuidChecker.findUUIDBasedOnPlayerName("Notch")).thenReturn(UUID.randomUUID());
        when(argumentParser.getArgumentsInsideDoubleQuotes(any(String[].class)))
                .thenReturn(new ArrayList<>(Arrays.asList("hello")));
        // The factory hands back null when every message ID in the configured range is taken.
        when(messageFactory.createPlayerMessage(any(UUID.class), any(UUID.class), anyString())).thenReturn(null);

        boolean result = sendCommand.execute(player, new String[]{"Notch", "\"hello\""});

        assertFalse(result);
        verify(player).sendMessage(contains("Your message couldn't be created."));
        verify(mailService, never()).sendMessage(any());
    }

    @Test
    public void testExecuteTellsSenderWhenRecipientHasNoMailbox() {
        stubCreatedMessage();
        when(mailService.sendMessage(message)).thenReturn(false);

        boolean result = sendCommand.execute(player, new String[]{"Notch", "\"hello\""});

        assertFalse(result);
        verify(player).sendMessage(contains("Notch doesn't have a mailbox yet"));
        verify(player, never()).sendMessage(contains("Sent."));
    }

    @Test
    public void testExecuteLeavesItemInHandWhenRecipientHasNoMailbox() {
        stubCreatedMessage();
        stubItemInHand();
        when(mailService.sendMessage(message)).thenReturn(false);

        boolean result = sendCommand.execute(player, new String[]{"Notch", "\"hello\"", "-attach"});

        assertFalse(result);
        verify(inventory, never()).setItemInMainHand(any());
        verify(player, never()).sendMessage(contains("Attached"));
        verify(player, never()).sendMessage(contains("Sent."));
    }

    @Test
    public void testExecuteTakesItemFromHandOnceDelivered() {
        stubCreatedMessage();
        stubItemInHand();
        when(mailService.sendMessage(message)).thenReturn(true);

        boolean result = sendCommand.execute(player, new String[]{"Notch", "\"hello\"", "-attach"});

        assertTrue(result);
        verify(message).addAttachment(attachment);
        verify(inventory).setItemInMainHand(any(ItemStack.class));
        verify(player).sendMessage(contains("Attached 1x diamond sword"));
        verify(player).sendMessage(contains("Sent."));
    }

    private void stubCreatedMessage() {
        when(uuidChecker.findUUIDBasedOnPlayerName("Notch")).thenReturn(UUID.randomUUID());
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(argumentParser.getArgumentsInsideDoubleQuotes(any(String[].class)))
                .thenReturn(new ArrayList<>(Arrays.asList("hello")));
        when(messageFactory.createPlayerMessage(any(UUID.class), any(UUID.class), anyString())).thenReturn(message);
    }

    private void stubItemInHand() {
        when(player.hasPermission("mailboxes.send.attach")).thenReturn(true);
        when(configService.getBoolean("attachmentsEnabled")).thenReturn(true);
        when(configService.getInt("maxAttachmentStackSize")).thenReturn(64);
        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getItemInMainHand()).thenReturn(itemInHand);
        when(itemInHand.getType()).thenReturn(Material.DIAMOND_SWORD);
        when(itemInHand.getAmount()).thenReturn(1);
        when(itemInHand.clone()).thenReturn(attachment);
        when(attachment.getType()).thenReturn(Material.DIAMOND_SWORD);
        when(attachment.getAmount()).thenReturn(1);
    }
}
