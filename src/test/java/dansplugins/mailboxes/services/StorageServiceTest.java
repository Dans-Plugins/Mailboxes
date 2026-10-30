package dansplugins.mailboxes.services;

import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.PlayerMessage;
import dansplugins.mailboxes.objects.PluginMessage;
import dansplugins.mailboxes.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
