package dansplugins.mailboxes.listeners;

import dansplugins.mailboxes.services.MailboxService;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class JoinListenerTest {

    @Mock
    private MailboxService mailboxService;

    @Mock
    private Player player;

    private JoinListener joinListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        joinListener = new JoinListener(mailboxService);
    }

    @Test
    public void testJoiningPlayerIsAssignedAMailboxBeforeBeingAlerted() {
        joinListener.handle(new PlayerJoinEvent(player, "joined"));

        InOrder inOrder = inOrder(mailboxService);
        inOrder.verify(mailboxService).assignMailboxToPlayerIfNecessary(player);
        inOrder.verify(mailboxService).alertPlayerIfTheyHaveUnreadMessages(player);
        verifyNoMoreInteractions(mailboxService);
    }
}
