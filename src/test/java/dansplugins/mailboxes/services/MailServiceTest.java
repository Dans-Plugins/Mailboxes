package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.MockServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.*;

public class MailServiceTest {

    private static final int MAILBOX_ID = 42;
    private static final String RECEIVED_NOTICE = ChatColor.AQUA + "You've received a message. Type /m list to view your messages.";

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    @Mock
    private PersistentData.LookupService lookupService;

    @Mock
    private Message message;

    @Mock
    private Player recipient;

    private MailService mailService;

    // Fresh UUIDs per test keep the shared MockServer's getPlayer stubs from leaking between tests.
    private UUID senderUUID;
    private UUID recipientUUID;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mailboxes is final and so cannot be mocked; MailService only consults it from
        // sendWelcomeMessage, which these tests do not exercise.
        mailService = new MailService(null, logger, lookupService);
        senderUUID = UUID.randomUUID();
        recipientUUID = UUID.randomUUID();
    }

    @Test
    public void testSendMessageWithNullMessage() {
        // MessageFactory returns null when no free message ID is available, so this is a
        // reachable state rather than a defensive one.
        assertFalse(mailService.sendMessage(null));
        verifyNoInteractions(lookupService);
    }

    @Test
    public void testSendMessageWithAMessageOfNoKnownType() {
        assertFalse(mailService.sendMessage(message));
    }

    @Test
    public void testSendPlayerMessageDeliversToRecipientMailboxAndNotifiesOnlineRecipient() {
        Mailbox mailbox = recipientMailbox();
        recipientIsOnline();
        PlayerMessage playerMessage = playerMessage(5);

        assertTrue(mailService.sendMessage(playerMessage));

        assertEquals(MAILBOX_ID, playerMessage.getMailboxID());
        assertSame(playerMessage, mailbox.getActiveMessage(5));
        verify(recipient).sendMessage(RECEIVED_NOTICE);
    }

    @Test
    public void testSendPlayerMessageFailsWhenRecipientHasNoMailbox() {
        when(lookupService.lookup(recipientUUID)).thenReturn(null);
        PlayerMessage playerMessage = playerMessage(5);

        assertFalse(mailService.sendMessage(playerMessage));

        assertEquals(0, playerMessage.getMailboxID());
        verify(logger).log("Mailbox was null.");
    }

    @Test
    public void testSendPlayerMessageToOfflineRecipientIsStillDelivered() {
        Mailbox mailbox = recipientMailbox();
        recipientIsOffline();
        PlayerMessage playerMessage = playerMessage(5);

        // The notice is skipped for an offline recipient; the message still lands in the mailbox.
        assertTrue(mailService.sendMessage(playerMessage));

        assertSame(playerMessage, mailbox.getActiveMessage(5));
    }

    @Test
    public void testSendPluginMessageDeliversToRecipientMailboxAndNotifiesOnlineRecipient() {
        Mailbox mailbox = recipientMailbox();
        recipientIsOnline();
        PluginMessage pluginMessage = pluginMessage(6);

        assertTrue(mailService.sendMessage(pluginMessage));

        assertEquals(MAILBOX_ID, pluginMessage.getMailboxID());
        assertSame(pluginMessage, mailbox.getActiveMessage(6));
        verify(recipient).sendMessage(RECEIVED_NOTICE);
    }

    @Test
    public void testSendPluginMessageFailsWhenRecipientHasNoMailbox() {
        when(lookupService.lookup(recipientUUID)).thenReturn(null);
        PluginMessage pluginMessage = pluginMessage(6);

        assertFalse(mailService.sendMessage(pluginMessage));

        assertEquals(0, pluginMessage.getMailboxID());
        verify(logger).log("Mailbox was null.");
    }

    @Test
    public void testSendPluginMessageToOfflineRecipientIsStillDelivered() {
        Mailbox mailbox = recipientMailbox();
        recipientIsOffline();
        PluginMessage pluginMessage = pluginMessage(6);

        assertTrue(mailService.sendMessage(pluginMessage));

        assertSame(pluginMessage, mailbox.getActiveMessage(6));
    }

    private Mailbox recipientMailbox() {
        Mailbox mailbox = new Mailbox(logger, MAILBOX_ID, recipientUUID);
        when(lookupService.lookup(recipientUUID)).thenReturn(mailbox);
        return mailbox;
    }

    private void recipientIsOnline() {
        assumeTrue("another test installed a different Bukkit server", MockServer.install());
        when(MockServer.SERVER.getPlayer(recipientUUID)).thenReturn(recipient);
    }

    private void recipientIsOffline() {
        assumeTrue("another test installed a different Bukkit server", MockServer.install());
        when(MockServer.SERVER.getPlayer(recipientUUID)).thenReturn(null);
    }

    private PlayerMessage playerMessage(int id) {
        return new PlayerMessage(id, "Alice", "Bob", "Hello", senderUUID, recipientUUID, logger, configService);
    }

    private PluginMessage pluginMessage(int id) {
        return new PluginMessage(id, "SomePlugin", "Bob", "Notice", recipientUUID, logger, configService);
    }
}
