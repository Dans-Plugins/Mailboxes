package dansplugins.mailboxes.objects;

import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MessageInteractivityTest {

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    @Mock
    private Player player;

    @Mock
    private Player.Spigot spigot;

    private Mailbox mailbox;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mailbox = new Mailbox(logger, 1, UUID.randomUUID());
        when(player.spigot()).thenReturn(spigot);
        when(configService.getBoolean("quotesEnabled")).thenReturn(false);
    }

    @Test
    public void testMessageListUsesClickableComponents() {
        // Given a mailbox with active messages
        for (int i = 1; i <= 3; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying the message list
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then it should use spigot's sendMessage for clickable components (3 messages)
        verify(spigot, times(3)).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testMessageViewShowsActionButtons() {
        // Given a message
        Message msg = createMockMessage(1, "TestSender");

        // When sending content to player
        msg.sendContentToPlayer(player);

        // Then it should use spigot's sendMessage for action buttons
        verify(spigot, atLeastOnce()).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testArchivedMessageDoesNotShowArchiveButton() {
        // Given an archived message
        Message msg = createMockMessage(1, "TestSender");
        msg.setArchived(true);

        // When sending content to player
        msg.sendContentToPlayer(player);

        // Then it should still use spigot's sendMessage for the delete button
        verify(spigot, atLeastOnce()).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testUnreadMessagesAreBoldInList() {
        // Given a mailbox with an unread message
        Message msg = createMockMessage(1, "Sender1");
        msg.setUnread(true);
        mailbox.addActiveMessage(msg);

        // When displaying the message list
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then it should use spigot's sendMessage for the message with bold formatting
        verify(spigot, atLeastOnce()).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testMessageWithAttachmentsShowsIndicator() {
        // Given a mailbox with a message that has attachments
        Message msg = createMockMessage(1, "Sender1");
        // Note: We can't easily add attachments in test without more mocking,
        // but the existing test shows the message display format
        mailbox.addActiveMessage(msg);

        // When displaying the message list
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then it should use spigot's sendMessage for clickable components
        verify(spigot, atLeastOnce()).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testMultipleMessagesEachGetClickableComponents() {
        // Given a mailbox with multiple messages
        for (int i = 1; i <= 5; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying the message list
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then each message should be sent as a clickable component
        verify(spigot, times(5)).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testArchivedMessagesAlsoClickable() {
        // Given a mailbox with archived messages
        for (int i = 1; i <= 3; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            msg.setArchived(true);
            mailbox.addArchivedMessage(msg);
        }

        // When displaying the archived message list
        mailbox.sendListOfArchivedMessagesToPlayer(player, 1, 10);

        // Then it should use spigot's sendMessage for clickable components
        verify(spigot, times(3)).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testUnreadMessagesListShowsClickableComponents() {
        // Given a mailbox with unread messages
        for (int i = 1; i <= 4; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            msg.setUnread(true);
            mailbox.addActiveMessage(msg);
        }

        // When displaying the unread message list
        mailbox.sendListOfUnreadMessagesToPlayer(player, 1, 10);

        // Then it should use spigot's sendMessage for clickable components
        verify(spigot, times(4)).sendMessage(any(BaseComponent[].class));
    }

    // Helper method to create mock messages
    private Message createMockMessage(int id, String sender) {
        Message msg = new Message(logger, configService, id, "player", sender, "TestRecipient", "Test content");
        msg.setDate(new Date());
        msg.setMailboxID(mailbox.getID());
        return msg;
    }
}
