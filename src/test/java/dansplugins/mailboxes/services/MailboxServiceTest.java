package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.factories.MailboxFactory;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MailboxServiceTest {

    @Mock
    private PersistentData persistentData;

    @Mock
    private MailboxFactory mailboxFactory;

    @Mock
    private ConfigService configService;

    @Mock
    private MailService mailService;

    @Mock
    private Logger logger;

    @Mock
    private Player player;

    @Mock
    private Mailbox mailbox;

    private final UUID playerUUID = UUID.randomUUID();

    private MailboxService mailboxService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mailboxService = new MailboxService(persistentData, mailboxFactory, configService, mailService, logger);
        when(player.getUniqueId()).thenReturn(playerUUID);
    }

    @Test
    public void testAssignMailboxWhenPlayerAlreadyHasOne() {
        // Given a player who already has a mailbox
        when(persistentData.getMailbox(playerUUID)).thenReturn(mailbox);

        // When assignment is attempted
        mailboxService.assignMailboxToPlayerIfNecessary(player);

        // Then nothing is created, stored, or sent
        verifyNoInteractions(mailboxFactory, mailService);
        verify(persistentData, never()).addMailbox(any());
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testAssignMailboxCreatesAndStoresANewMailbox() {
        // Given a player without a mailbox and both join notices disabled
        when(persistentData.getMailbox(playerUUID)).thenReturn(null);
        when(mailboxFactory.createMailbox(player)).thenReturn(mailbox);

        // When assignment is attempted
        mailboxService.assignMailboxToPlayerIfNecessary(player);

        // Then the factory's mailbox is stored and the player is told nothing
        verify(persistentData).addMailbox(mailbox);
        verify(player, never()).sendMessage(anyString());
        verifyNoInteractions(mailService);
    }

    @Test
    public void testAssignMailboxSendsAssignmentAlertWhenEnabled() {
        when(persistentData.getMailbox(playerUUID)).thenReturn(null);
        when(mailboxFactory.createMailbox(player)).thenReturn(mailbox);
        when(configService.getBoolean("assignmentAlertEnabled")).thenReturn(true);

        mailboxService.assignMailboxToPlayerIfNecessary(player);

        verify(player).sendMessage(ChatColor.AQUA + "You have been assigned a mailbox. Type /m help for help.");
        verifyNoInteractions(mailService);
    }

    @Test
    public void testAssignMailboxSendsWelcomeMessageWhenEnabled() {
        when(persistentData.getMailbox(playerUUID)).thenReturn(null);
        when(mailboxFactory.createMailbox(player)).thenReturn(mailbox);
        when(configService.getBoolean("welcomeMessageEnabled")).thenReturn(true);

        mailboxService.assignMailboxToPlayerIfNecessary(player);

        verify(mailService).sendWelcomeMessage(player);
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testAssignMailboxStoresMailboxBeforeSendingWelcomeMessage() {
        // The welcome message is delivered into the new mailbox, so it has to be stored first
        when(persistentData.getMailbox(playerUUID)).thenReturn(null);
        when(mailboxFactory.createMailbox(player)).thenReturn(mailbox);
        when(configService.getBoolean("welcomeMessageEnabled")).thenReturn(true);

        mailboxService.assignMailboxToPlayerIfNecessary(player);

        InOrder inOrder = inOrder(persistentData, mailService);
        inOrder.verify(persistentData).addMailbox(mailbox);
        inOrder.verify(mailService).sendWelcomeMessage(player);
    }

    @Test
    public void testUnreadAlertDoesNothingWhenDisabled() {
        // Given the unread-messages alert is disabled
        when(configService.getBoolean("unreadMessagesAlertEnabled")).thenReturn(false);

        // When the alert is checked
        mailboxService.alertPlayerIfTheyHaveUnreadMessages(player);

        // Then the mailbox is not even looked up
        verifyNoInteractions(persistentData, logger);
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testUnreadAlertLogsErrorWhenPlayerHasNoMailbox() {
        when(configService.getBoolean("unreadMessagesAlertEnabled")).thenReturn(true);
        when(persistentData.getMailbox(player)).thenReturn(null);

        mailboxService.alertPlayerIfTheyHaveUnreadMessages(player);

        verify(logger).log("ERROR: Mailbox is null.");
        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testUnreadAlertSilentWhenNoUnreadMessages() {
        when(configService.getBoolean("unreadMessagesAlertEnabled")).thenReturn(true);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
        when(mailbox.containsUnreadMessages()).thenReturn(false);

        mailboxService.alertPlayerIfTheyHaveUnreadMessages(player);

        verify(player, never()).sendMessage(anyString());
    }

    @Test
    public void testUnreadAlertReportsUnreadCount() {
        when(configService.getBoolean("unreadMessagesAlertEnabled")).thenReturn(true);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
        when(mailbox.containsUnreadMessages()).thenReturn(true);
        when(mailbox.getUnreadMessages()).thenReturn(new ArrayList<>(Arrays.asList(
                mock(Message.class), mock(Message.class), mock(Message.class))));

        mailboxService.alertPlayerIfTheyHaveUnreadMessages(player);

        verify(player).sendMessage(ChatColor.GREEN + "You have 3 unread messages in your mailbox. Type /m list unread to view them.");
    }
}
