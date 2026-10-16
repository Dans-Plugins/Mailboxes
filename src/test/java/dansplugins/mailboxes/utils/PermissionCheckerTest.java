package dansplugins.mailboxes.utils;

import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PermissionCheckerTest {

    @Mock
    private CommandSender sender;

    private PermissionChecker permissionChecker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        permissionChecker = new PermissionChecker();
    }

    @Test
    public void testSenderWithPermissionPassesSilently() {
        when(sender.hasPermission("mailboxes.list")).thenReturn(true);

        assertTrue(permissionChecker.checkPermission(sender, "mailboxes.list"));
        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    public void testSenderWithoutPermissionIsToldWhichNodeIsMissing() {
        when(sender.hasPermission("mailboxes.list")).thenReturn(false);

        assertFalse(permissionChecker.checkPermission(sender, "mailboxes.list"));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sender).sendMessage(captor.capture());
        assertTrue(captor.getValue().contains("'mailboxes.list'"));
    }
}
