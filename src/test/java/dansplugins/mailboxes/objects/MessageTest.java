package dansplugins.mailboxes.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import dansplugins.mailboxes.utils.MockServer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.UnsafeValues;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.Invocation;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
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

    /**
     * The real {@code ItemMeta} implementation only exists on a server, so these stubs stand in
     * for it the way a server does: as a {@code ConfigurationSerializable} the item carries.
     */
    private ItemStack itemWithDisplayName(String displayName) {
        assumeTrue(MockServer.install());
        ItemFactory itemFactory = mock(ItemFactory.class);
        when(itemFactory.isApplicable(any(ItemMeta.class), any(Material.class))).thenReturn(true);
        when(itemFactory.asMetaFor(any(ItemMeta.class), any(Material.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(itemFactory.updateMaterial(any(ItemMeta.class), any(Material.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));
        UnsafeValues unsafe = mock(UnsafeValues.class);
        when(unsafe.getDataVersion()).thenReturn(1631);
        when(MockServer.SERVER.getItemFactory()).thenReturn(itemFactory);
        when(MockServer.SERVER.getUnsafe()).thenReturn(unsafe);
        ConfigurationSerialization.registerClass(StubItemMeta.class);

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 3);
        assertTrue(item.setItemMeta(new StubItemMeta(displayName)));
        return item;
    }

    /** Passes save data through the same JSON encoding {@code StorageService} writes and reads. */
    private Map<String, String> throughStorageFileJson(Map<String, String> data) {
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        return new Gson().fromJson(new Gson().toJson(data), type);
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

        Map<String, String> data = message.save();

        assertFalse(data.containsKey("attachments"));
        assertFalse(data.containsKey("attachmentItems"));
    }

    @Test
    public void testAttachmentMetadataSurvivesTheStorageFileRoundTrip() {
        ItemStack item = itemWithDisplayName("Excalibur");
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi", 7);
        original.addAttachment(item);

        Message loaded = new Message(throughStorageFileJson(original.save()), logger, configService);

        assertEquals(1, loaded.getAttachments().size());
        ItemStack loadedItem = loaded.getAttachments().get(0);
        assertEquals(Material.DIAMOND_SWORD, loadedItem.getType());
        assertEquals(3, loadedItem.getAmount());
        assertTrue(loadedItem.hasItemMeta());
        assertEquals("Excalibur", loadedItem.getItemMeta().getDisplayName());
        verify(logger, never()).logError(anyString());
    }

    @Test
    public void testSaveNoLongerWritesTheLegacyAttachmentsKey() {
        Message message = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        message.addAttachment(itemWithDisplayName("Excalibur"));

        Map<String, String> data = message.save();

        assertTrue(data.containsKey("attachmentItems"));
        assertFalse(data.containsKey("attachments"));
    }

    @Test
    public void testLoadStillReadsTheLegacyAttachmentsKey() {
        itemWithDisplayName("installs the stubbed server");
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        data.put("attachments", "[{\"v\":1631.0,\"type\":\"DIAMOND_SWORD\",\"amount\":2.0}]");

        Message loaded = new Message(data, logger, configService);

        assertEquals(1, loaded.getAttachments().size());
        assertEquals(Material.DIAMOND_SWORD, loaded.getAttachments().get(0).getType());
        assertEquals(2, loaded.getAttachments().get(0).getAmount());
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
        verify(logger).logError(startsWith("Error loading attachments of message 1: "));
    }

    @Test
    public void testLoadWithMalformedAttachmentItemsLogsAndFallsBackToNoAttachments() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        data.put("attachmentItems", new Gson().toJson("items: [unclosed"));

        Message loaded = new Message(data, logger, configService);

        assertFalse(loaded.hasAttachments());
        verify(logger).logError(startsWith("Error loading attachments of message 1: "));
    }

    @Test
    public void testLoadWithAttachmentItemsThatAreNotItemsLogsAndFallsBackToNoAttachments() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        data.put("attachmentItems", new Gson().toJson("items:\n- not an item\n"));

        Message loaded = new Message(data, logger, configService);

        assertFalse(loaded.hasAttachments());
        verify(logger).logError(startsWith("Error loading attachments of message 1: "));
    }

    @Test
    public void testSaveWritesBackUnreadableAttachmentItemsUnchanged() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        String unreadable = new Gson().toJson("items:\n- not an item\n");
        data.put("attachmentItems", unreadable);

        Message loaded = new Message(throughStorageFileJson(data), logger, configService);
        Map<String, String> resaved = loaded.save();

        assertEquals(unreadable, resaved.get("attachmentItems"));
        assertFalse(resaved.containsKey("attachments"));
    }

    @Test
    public void testSaveWritesBackMalformedAttachmentItemsUnchanged() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        String malformed = new Gson().toJson("items: [unclosed");
        data.put("attachmentItems", malformed);

        Map<String, String> resaved = new Message(data, logger, configService).save();

        assertEquals(malformed, resaved.get("attachmentItems"));
    }

    @Test
    public void testSaveWritesBackUnreadableLegacyAttachmentsUnderTheLegacyKey() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        data.put("attachments", "not a list");

        Map<String, String> resaved = new Message(data, logger, configService).save();

        assertEquals("not a list", resaved.get("attachments"));
        assertFalse(resaved.containsKey("attachmentItems"));
    }

    @Test
    public void testUnreadableAttachmentDataSurvivesRepeatedSaveAndLoad() {
        Message original = new Message(logger, configService, 1, "Player Message", "Alice", "Bob", "Hi");
        Map<String, String> data = original.save();
        String unreadable = new Gson().toJson("items:\n- not an item\n");
        data.put("attachmentItems", unreadable);

        Message first = new Message(throughStorageFileJson(data), logger, configService);
        Message second = new Message(throughStorageFileJson(first.save()), logger, configService);

        assertEquals(unreadable, second.save().get("attachmentItems"));
        verify(logger, times(2)).logError(startsWith("Error loading attachments of message 1: "));
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
