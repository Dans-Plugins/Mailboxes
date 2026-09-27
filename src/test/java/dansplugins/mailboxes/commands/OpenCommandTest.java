package dansplugins.mailboxes.commands;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OpenCommandTest {

    @Mock
    private Logger logger;

    @Mock
    private PersistentData persistentData;

    @Mock
    private Player player;

    @Mock
    private PlayerInventory inventory;

    @Mock
    private Mailbox mailbox;

    @Mock
    private Message message;

    @Mock
    private Message archivedMessage;

    @Mock
    private CommandSender nonPlayerSender;

    @Mock
    private ItemStack firstItem;

    @Mock
    private ItemStack secondItem;

    private OpenCommand openCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        openCommand = new OpenCommand(logger, persistentData);
        when(persistentData.getMailbox(player)).thenReturn(mailbox);
        when(player.getInventory()).thenReturn(inventory);
    }

    @Test
    public void testExecuteWithNonPlayerSender() {
        boolean result = openCommand.execute(nonPlayerSender, new String[]{"1"});

        assertFalse(result);
        verify(logger).log("Only players can use this command.");
    }

    @Test
    public void testExecuteWithNoArgs() {
        boolean result = openCommand.execute(player, new String[]{});

        assertFalse(result);
        verify(player).sendMessage(contains("Usage: /m open (ID)"));
    }

    @Test
    public void testExecuteWithInvalidID() {
        boolean result = openCommand.execute(player, new String[]{"abc"});

        assertFalse(result);
        verify(player).sendMessage(contains("Invalid message ID: abc"));
        verify(persistentData, never()).getMailbox(player);
    }

    @Test
    public void testExecuteWithNoMailbox() {
        when(persistentData.getMailbox(player)).thenReturn(null);

        boolean result = openCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("Error: Mailbox wasn't found."));
    }

    @Test
    public void testExecuteWithMessageNotFound() {
        when(mailbox.getMessage(1)).thenReturn(null);

        boolean result = openCommand.execute(player, new String[]{"1"});

        assertFalse(result);
        verify(player).sendMessage(contains("A message with that ID wasn't found."));
    }

    @Test
    public void testExecuteShowsMessageAndMarksItRead() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.hasAttachments()).thenReturn(false);

        boolean result = openCommand.execute(player, new String[]{"1"});

        assertTrue(result);
        verify(message).sendContentToPlayer(player);
        verify(message).setUnread(false);
        verify(player, never()).getInventory();
    }

    @Test
    public void testExecuteDeliversAllAttachmentsAndClearsThem() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.hasAttachments()).thenReturn(true);
        when(message.getAttachments()).thenReturn(Arrays.asList(firstItem, secondItem));
        when(inventory.addItem(firstItem, secondItem)).thenReturn(new HashMap<>());

        boolean result = openCommand.execute(player, new String[]{"1"});

        assertTrue(result);
        verify(inventory).addItem(firstItem, secondItem);
        verify(player).sendMessage(contains("All attached items have been added to your inventory."));
        verify(message).setAttachments(Collections.emptyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteKeepsAttachmentsThatDoNotFit() {
        when(mailbox.getMessage(1)).thenReturn(message);
        when(message.hasAttachments()).thenReturn(true);
        when(message.getAttachments()).thenReturn(Arrays.asList(firstItem, secondItem));
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        leftover.put(1, secondItem);
        when(inventory.addItem(firstItem, secondItem)).thenReturn(leftover);

        boolean result = openCommand.execute(player, new String[]{"1"});

        assertTrue(result);
        verify(player).sendMessage(contains("Some items couldn't fit in your inventory"));
        ArgumentCaptor<List<ItemStack>> captor = ArgumentCaptor.forClass(List.class);
        verify(message).setAttachments(captor.capture());
        assertEquals(Collections.singletonList(secondItem), captor.getValue());
    }

    @Test
    public void testTabCompletionsIncludeActiveAndArchivedIDsMatchingPrefix() {
        when(message.getID()).thenReturn(12);
        when(archivedMessage.getID()).thenReturn(15);
        Message other = mock(Message.class);
        when(other.getID()).thenReturn(30);
        when(mailbox.getActiveMessages()).thenReturn(new ArrayList<>(Arrays.asList(message, other)));
        when(mailbox.getArchivedMessages()).thenReturn(new ArrayList<>(Collections.singletonList(archivedMessage)));

        List<String> completions = openCommand.getTabCompletions(player, new String[]{"open", "1"});

        assertEquals(2, completions.size());
        assertTrue(completions.contains("12"));
        assertTrue(completions.contains("15"));
    }

    @Test
    public void testTabCompletionsEmptyForNonPlayer() {
        List<String> completions = openCommand.getTabCompletions(nonPlayerSender, new String[]{"open", ""});

        assertTrue(completions.isEmpty());
    }
}
