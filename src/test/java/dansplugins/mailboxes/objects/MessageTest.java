package dansplugins.mailboxes.objects;

import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.Invocation;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MessageTest {

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    @Mock
    private Player player;

    @Mock
    private Player.Spigot spigot;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(player.spigot()).thenReturn(spigot);
    }

    @Test
    public void testNewMessageIsUnreadAndNotArchived() {
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");

        assertTrue(message.isUnread());
        assertFalse(message.isArchived());
        assertFalse(message.hasAttachments());
        assertNotNull(message.getDate());
    }

    @Test
    public void testConstructorWithMailboxIDSetsMailboxID() {
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi", 42);

        assertEquals(42, message.getMailboxID());
    }

    @Test
    public void testSaveAndLoadRoundTripPreservesFields() {
        Message original = new Message(logger, configService, 123, "Player Message", "Alice", "Bob", "Hello there", 7);
        original.setArchived(true);
        original.setUnread(false);

        Message loaded = new Message(original.save(), logger, configService);

        assertEquals(123, loaded.getID());
        assertEquals("Player Message", loaded.getType());
        assertEquals("Alice", loaded.getSender());
        assertEquals("Bob", loaded.getRecipient());
        assertEquals("Hello there", loaded.getContent());
        assertEquals(7, loaded.getMailboxID());
        assertTrue(loaded.isArchived());
        assertFalse(loaded.isUnread());
        assertFalse(loaded.hasAttachments());
    }

    @Test
    public void testSaveAndLoadRoundTripPreservesDateToTheSecond() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Date date = new Date(1700000000123L);
        original.setDate(date);

        Message loaded = new Message(original.save(), logger, configService);

        // Gson's default date format has second precision, so milliseconds are dropped
        assertEquals(1700000000000L, loaded.getDate().getTime());
    }

    @Test
    public void testSaveOmitsAttachmentsKeyWhenThereAreNone() {
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");

        assertFalse(message.save().containsKey("attachments"));
    }

    @Test
    public void testLoadWithoutUnreadOrArchivedKeysTreatsMessageAsReadAndActive() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        data.remove("unread");
        data.remove("archived");

        Message loaded = new Message(data, logger, configService);

        assertFalse(loaded.isUnread());
        assertFalse(loaded.isArchived());
    }

    @Test
    public void testLoadWithMalformedAttachmentsLogsAndFallsBackToNoAttachments() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        data.put("attachments", "not a list");

        Message loaded = new Message(data, logger, configService);

        assertFalse(loaded.hasAttachments());
        verify(logger).log(startsWith("Error loading attachments: "));
    }

    @Test
    public void testGetAttachmentsReturnsDefensiveCopy() {
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        message.addAttachment(mock(ItemStack.class));

        message.getAttachments().clear();

        assertEquals(1, message.getAttachments().size());
    }

    @Test
    public void testNullAttachmentsAreTreatedAsEmpty() {
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        message.setAttachments(null);

        assertFalse(message.hasAttachments());
        assertTrue(message.getAttachments().isEmpty());

        message.addAttachment(mock(ItemStack.class));
        assertTrue(message.hasAttachments());
    }

    @Test
    public void testSendContentToPlayerQuotesContentWhenQuotesEnabled() {
        when(configService.getBoolean("quotesEnabled")).thenReturn(true);
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");

        message.sendContentToPlayer(player);

        verify(player).sendMessage(contains("\"Hi\""));
    }

    @Test
    public void testSendContentToPlayerOmitsQuotesWhenQuotesDisabled() {
        when(configService.getBoolean("quotesEnabled")).thenReturn(false);
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");

        message.sendContentToPlayer(player);

        verify(player).sendMessage(endsWith("Hi"));
        verify(player, never()).sendMessage(contains("\"Hi\""));
    }

    @Test
    public void testSendContentToPlayerListsAttachments() {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(Material.DIAMOND_SWORD);
        when(item.getAmount()).thenReturn(2);
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        message.addAttachment(item);

        message.sendContentToPlayer(player);

        verify(player).sendMessage(contains("Attachments (1):"));
        verify(player).sendMessage(contains("2x diamond sword"));
    }

    @Test
    public void testSendContentToPlayerShowsArchiveAndDeleteButtonsForActiveMessage() {
        Message message = new Message(logger, configService, 55, "Player Message", "Alice", "Bob", "Hi");

        message.sendContentToPlayer(player);

        String actions = captureActionLine();
        assertTrue(actions.contains("[Archive]"));
        assertTrue(actions.contains("[Delete]"));
    }

    @Test
    public void testSendContentToPlayerHidesArchiveButtonForArchivedMessage() {
        Message message = new Message(logger, configService, 55, "Player Message", "Alice", "Bob", "Hi");
        message.setArchived(true);

        message.sendContentToPlayer(player);

        String actions = captureActionLine();
        assertFalse(actions.contains("[Archive]"));
        assertTrue(actions.contains("[Delete]"));
    }

    @Test
    public void testPlayerMessageSaveAndLoadRoundTripPreservesUUIDs() {
        UUID senderUUID = UUID.randomUUID();
        UUID recipientUUID = UUID.randomUUID();
        PlayerMessage original = new PlayerMessage(9, "Alice", "Bob", "Hi", senderUUID, recipientUUID, logger, configService);

        PlayerMessage loaded = new PlayerMessage(original.save(), logger, configService);

        assertEquals("Player Message", loaded.getType());
        assertEquals(senderUUID, loaded.getSenderUUID());
        assertEquals(recipientUUID, loaded.getRecipientUUID());
        assertEquals("Hi", loaded.getContent());
    }

    @Test
    public void testPluginMessageSaveAndLoadRoundTripPreservesRecipientUUID() {
        UUID recipientUUID = UUID.randomUUID();
        PluginMessage original = new PluginMessage(10, "SomePlugin", "Bob", "Hi", recipientUUID, logger, configService);

        PluginMessage loaded = new PluginMessage(original.save(), logger, configService);

        assertEquals("Plugin Message", loaded.getType());
        assertEquals("SomePlugin", loaded.getSender());
        assertEquals(recipientUUID, loaded.getRecipientUUID());
        assertFalse(original.save().containsKey("senderUUID"));
    }

    private String captureActionLine() {
        // The action line has a variable number of components, so read the raw varargs of the single call
        Invocation invocation = mockingDetails(spigot).getInvocations().iterator().next();
        StringBuilder text = new StringBuilder();
        for (Object component : invocation.getRawArguments()) {
            for (BaseComponent part : (BaseComponent[]) component) {
                text.append(part.toPlainText());
            }
        }
        return text.toString();
    }
}
