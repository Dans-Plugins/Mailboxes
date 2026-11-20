package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    private String filePath;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        filePath = temporaryFolder.newFolder("Mailboxes").getPath() + File.separator;
    }

    private StorageService newStorageService(PersistentData persistentData) {
        return new StorageService(configService, null, persistentData, logger, filePath);
    }

    private StorageService newLoadedStorageService(PersistentData persistentData) {
        StorageService storageService = newStorageService(persistentData);
        assertTrue(storageService.load());
        return storageService;
    }

    private File file(String name) {
        return new File(filePath, name);
    }

    private static void write(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] read(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /** A mailbox with one active player message and one archived plugin message, saved to disk. */
    private Mailbox saveOneMailbox(StorageService storageService, PersistentData persistentData, UUID owner) {
        Mailbox mailbox = new Mailbox(logger, 7, owner);
        PlayerMessage active = new PlayerMessage(2, "Alice", "Bob", "See you at the docks", UUID.randomUUID(), owner, logger, configService);
        active.setMailboxID(7);
        mailbox.addActiveMessage(active);
        PluginMessage archived = new PluginMessage(3, "SomePlugin", "Bob", "Your plot expires soon", owner, logger, configService);
        archived.setMailboxID(7);
        mailbox.addArchivedMessage(archived);
        persistentData.addMailbox(mailbox);
        assertTrue(storageService.save());
        return mailbox;
    }

    @Test
    public void testMailboxesAndMessagesSurviveASaveAndLoadRoundTrip() {
        // prepare
        PersistentData persistentData = new PersistentData(logger);
        UUID owner = UUID.randomUUID();
        saveOneMailbox(newLoadedStorageService(persistentData), persistentData, owner);
        PersistentData restored = new PersistentData(logger);

        // execute
        boolean loaded = newStorageService(restored).load();

        // verify
        assertTrue(loaded);
        assertEquals(1, restored.getMailboxes().size());
        Mailbox mailbox = restored.getMailbox(owner);
        assertNotNull(mailbox);
        assertEquals(7, mailbox.getID());
        assertEquals(1, mailbox.getActiveMessages().size());
        Message active = mailbox.getActiveMessages().get(0);
        assertTrue(active instanceof PlayerMessage);
        assertEquals(2, active.getID());
        assertEquals("Alice", active.getSender());
        assertEquals("See you at the docks", active.getContent());
        assertEquals(1, mailbox.getArchivedMessages().size());
        Message archived = mailbox.getArchivedMessages().get(0);
        assertTrue(archived instanceof PluginMessage);
        assertEquals("Your plot expires soon", archived.getContent());
    }

    @Test
    public void testSaveBeforeAnyLoadIsRefusedAndLeavesTheFilesAlone() throws IOException {
        // prepare - data from a previous run; onEnable() failed before load() ran
        String previousRun = "[{\"ID\":\"\\\"7\\\"\"}]";
        write(file("mailboxes.json"), previousRun);
        StorageService storageService = newStorageService(new PersistentData(logger));

        // execute
        boolean saved = storageService.save();

        // verify
        assertFalse(saved);
        assertTrue(storageService.isLoadFailed());
        assertEquals(previousRun, new String(read(file("mailboxes.json")), StandardCharsets.UTF_8));
        assertFalse(file("activeMessages.json").exists());
    }

    @Test
    public void testTruncatedMessagesFileFailsTheLoadAndNoFileIsOverwritten() throws IOException {
        // prepare - a good save, then activeMessages.json cut off mid-write
        PersistentData persistentData = new PersistentData(logger);
        saveOneMailbox(newLoadedStorageService(persistentData), persistentData, UUID.randomUUID());
        String full = new String(read(file("activeMessages.json")), StandardCharsets.UTF_8);
        write(file("activeMessages.json"), full.substring(0, full.length() / 2));
        byte[] mailboxes = read(file("mailboxes.json"));
        byte[] active = read(file("activeMessages.json"));
        byte[] archived = read(file("archivedMessages.json"));

        PersistentData restarted = new PersistentData(logger);
        StorageService restartedService = newStorageService(restarted);

        // execute - load on the next start, then the autosave / onDisable save
        boolean loaded = restartedService.load();
        boolean saved = restartedService.save();

        // verify
        assertFalse(loaded);
        assertFalse(saved);
        assertTrue(restarted.getMailboxes().isEmpty());
        assertArrayEquals(mailboxes, read(file("mailboxes.json")));
        assertArrayEquals(active, read(file("activeMessages.json")));
        assertArrayEquals(archived, read(file("archivedMessages.json")));
    }

    @Test
    public void testMalformedMailboxFailsTheWholeLoadAndLeavesMemoryUntouched() throws IOException {
        // prepare - a loaded service holding one mailbox, then a mailbox whose owner UUID does not parse
        PersistentData persistentData = new PersistentData(logger);
        StorageService storageService = newLoadedStorageService(persistentData);
        saveOneMailbox(storageService, persistentData, UUID.randomUUID());
        write(file("mailboxes.json"), "[{\"ID\":\"\\\"8\\\"\",\"ownerUUID\":\"\\\"not-a-uuid\\\"\"}]");

        // execute
        boolean loaded = storageService.load();

        // verify
        assertFalse(loaded);
        assertEquals(1, persistentData.getMailboxes().size());
        assertEquals(1, persistentData.getMailboxes().get(0).getActiveMessages().size());
        assertFalse(storageService.save());
        assertTrue(new String(read(file("mailboxes.json")), StandardCharsets.UTF_8).contains("not-a-uuid"));
    }

    @Test
    public void testEmptyFilesLoadAsNoData() throws IOException {
        write(file("mailboxes.json"), "");
        write(file("activeMessages.json"), "");
        write(file("archivedMessages.json"), "");
        PersistentData persistentData = new PersistentData(logger);

        assertTrue(newStorageService(persistentData).load());
        assertTrue(persistentData.getMailboxes().isEmpty());
    }

    @Test
    public void testSaveLeavesNoTemporaryFilesBehind() {
        PersistentData persistentData = new PersistentData(logger);
        saveOneMailbox(newLoadedStorageService(persistentData), persistentData, UUID.randomUUID());

        String[] names = new File(filePath).list();
        assertNotNull(names);
        for (String name : names) {
            assertFalse(name, name.endsWith(".tmp"));
        }
    }

    @Test
    public void testParseMessagesBuildsSubclassMatchingEachType() {
        Map<String, String> defaultData = new Message(logger, configService, 1, "Default Message", "Server", "Bob", "Hi", 7).save();
        PlayerMessage playerMessage = new PlayerMessage(2, "Alice", "Bob", "Hello", UUID.randomUUID(), UUID.randomUUID(), logger, configService);
        playerMessage.setMailboxID(7);
        PluginMessage pluginMessage = new PluginMessage(3, "SomePlugin", "Bob", "Notice", UUID.randomUUID(), logger, configService);
        pluginMessage.setMailboxID(7);

        List<Map<String, String>> data = Arrays.asList(defaultData, playerMessage.save(), pluginMessage.save());

        ArrayList<Message> messages = StorageService.parseMessages(data, logger, configService);

        assertEquals(3, messages.size());
        assertEquals(Message.class, messages.get(0).getClass());
        assertTrue(messages.get(1) instanceof PlayerMessage);
        assertTrue(messages.get(2) instanceof PluginMessage);
        verify(logger, never()).log(anyString());
    }

    @Test
    public void testParseMessagesLogsAndSkipsUnsupportedType() {
        Map<String, String> unsupportedData = new Message(logger, configService, 1, "Carrier Pigeon", "Alice", "Bob", "Coo", 7).save();

        ArrayList<Message> messages = StorageService.parseMessages(Collections.singletonList(unsupportedData), logger, configService);

        assertTrue(messages.isEmpty());
        verify(logger).log("Unsupported message type encountered: Carrier Pigeon");
    }

    @Test
    public void testParseMessagesReturnsEmptyListForNoData() {
        ArrayList<Message> messages = StorageService.parseMessages(Collections.<Map<String, String>>emptyList(), logger, configService);

        assertTrue(messages.isEmpty());
    }
}
