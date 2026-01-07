package dansplugins.mailboxes.objects;

import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MailboxPaginationTest {

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
    }

    @Test
    public void testEmptyActiveMessagesShowsAppropriateMessage() {
        // Given a mailbox with no active messages
        // When displaying page 1
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then it should show appropriate message
        verify(player).sendMessage(contains("You don't have any active messages at this time."));
    }

    @Test
    public void testEmptyActiveMessagesWithHighPageShowsError() {
        // Given a mailbox with no active messages
        // When displaying page 2
        mailbox.sendListOfActiveMessagesToPlayer(player, 2, 10);

        // Then it should show error message
        verify(player).sendMessage(contains("Invalid page number. You don't have any active messages."));
    }

    @Test
    public void testSinglePageOfMessages() {
        // Given a mailbox with 5 active messages
        for (int i = 1; i <= 5; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying page 1 with page size 10
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then it should show header with page 1/1
        verify(player).sendMessage(contains("=== Active Messages (Page 1/1) ==="));
        // And should not show navigation links (only 1 page)
        verify(spigot, never()).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testMultiplePagesShowsNavigation() {
        // Given a mailbox with 15 active messages (will span 2 pages with page size 10)
        for (int i = 1; i <= 15; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying page 1 with page size 10
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 10);

        // Then it should show header with page 1/2
        verify(player).sendMessage(contains("=== Active Messages (Page 1/2) ==="));
        // And should show navigation links
        verify(spigot).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testPageBoundaryCalculation() {
        // Given a mailbox with 25 active messages (will span 3 pages with page size 10)
        for (int i = 1; i <= 25; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying page 2 with page size 10
        mailbox.sendListOfActiveMessagesToPlayer(player, 2, 10);

        // Then it should show header with page 2/3
        verify(player).sendMessage(contains("=== Active Messages (Page 2/3) ==="));
        // And it should display 10 messages (messages 11-20)
        // We verify by checking 10 message lines are sent (plus header and legend)
        verify(player, times(12)).sendMessage(anyString()); // 1 header + 1 legend + 10 messages
    }

    @Test
    public void testLastPageWithPartialMessages() {
        // Given a mailbox with 25 active messages (last page will have 5 messages)
        for (int i = 1; i <= 25; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying page 3 with page size 10
        mailbox.sendListOfActiveMessagesToPlayer(player, 3, 10);

        // Then it should show header with page 3/3
        verify(player).sendMessage(contains("=== Active Messages (Page 3/3) ==="));
        // And it should display 5 messages (messages 21-25)
        verify(player, times(7)).sendMessage(anyString()); // 1 header + 1 legend + 5 messages
    }

    @Test
    public void testInvalidPageNumberTooHigh() {
        // Given a mailbox with 5 active messages (only 1 page)
        for (int i = 1; i <= 5; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When trying to display page 2
        mailbox.sendListOfActiveMessagesToPlayer(player, 2, 10);

        // Then it should show error message
        verify(player).sendMessage(contains("Invalid page number. Valid pages: 1-1"));
    }

    @Test
    public void testInvalidPageNumberNegative() {
        // Given a mailbox with 5 active messages
        for (int i = 1; i <= 5; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When trying to display page 0
        mailbox.sendListOfActiveMessagesToPlayer(player, 0, 10);

        // Then it should show error message
        verify(player).sendMessage(contains("Invalid page number. Valid pages: 1-1"));
    }

    @Test
    public void testArchivedMessagesPagination() {
        // Given a mailbox with 15 archived messages
        for (int i = 1; i <= 15; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addArchivedMessage(msg);
        }

        // When displaying page 1 with page size 10
        mailbox.sendListOfArchivedMessagesToPlayer(player, 1, 10);

        // Then it should show archived messages header with page 1/2
        verify(player).sendMessage(contains("=== Archived Messages (Page 1/2) ==="));
        verify(spigot).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testUnreadMessagesPagination() {
        // Given a mailbox with 15 unread messages (mix of active and archived)
        for (int i = 1; i <= 10; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            msg.setUnread(true);
            mailbox.addActiveMessage(msg);
        }
        for (int i = 11; i <= 15; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            msg.setUnread(true);
            mailbox.addArchivedMessage(msg);
        }

        // When displaying page 1 with page size 10
        mailbox.sendListOfUnreadMessagesToPlayer(player, 1, 10);

        // Then it should show unread messages header with page 1/2
        verify(player).sendMessage(contains("=== Unread Messages (Page 1/2) ==="));
        verify(spigot).sendMessage(any(BaseComponent[].class));
    }

    @Test
    public void testPageSizeRespected() {
        // Given a mailbox with 20 active messages
        for (int i = 1; i <= 20; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When displaying page 1 with page size 5
        mailbox.sendListOfActiveMessagesToPlayer(player, 1, 5);

        // Then it should show page 1/4
        verify(player).sendMessage(contains("=== Active Messages (Page 1/4) ==="));
        // And it should display 5 messages
        verify(player, times(7)).sendMessage(anyString()); // 1 header + 1 legend + 5 messages
    }

    @Test
    public void testDefaultPageSizeUsedByOverloadedMethod() {
        // Given a mailbox with 15 active messages
        for (int i = 1; i <= 15; i++) {
            Message msg = createMockMessage(i, "Sender" + i);
            mailbox.addActiveMessage(msg);
        }

        // When calling the overloaded method without page parameters
        mailbox.sendListOfActiveMessagesToPlayer(player);

        // Then it should show page 1 with default page size (10)
        verify(player).sendMessage(contains("=== Active Messages (Page 1/2) ==="));
    }

    // Helper method to create mock messages
    private Message createMockMessage(int id, String sender) {
        Message msg = new Message(logger, configService, id, "player", sender, "TestRecipient", "Test content");
        msg.setDate(new Date());
        return msg;
    }
}
