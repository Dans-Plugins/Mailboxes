package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.services.ConfigService;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MailboxFactoryTest {
    private static final int ID_SPACE = 3;
    private static final int TAKEN_ID = 0;

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    @Mock
    private Player player;

    private final UUID playerUUID = UUID.randomUUID();

    private PersistentData persistentData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistentData = new PersistentData(logger);
        when(configService.getInt("maxMailboxIDNumber")).thenReturn(ID_SPACE);
        when(player.getUniqueId()).thenReturn(playerUUID);
    }

    /**
     * A factory whose random draws always land on ID 0 — taken in every setup below — so that the
     * behaviour once random draws are exhausted can be exercised deterministically.
     */
    private MailboxFactory mailboxFactoryAlwaysDrawingTakenID() {
        Random alwaysDrawsZero = new Random() {
            @Override
            public int nextInt(int bound) {
                return TAKEN_ID;
            }
        };
        return new MailboxFactory(configService, persistentData, logger, alwaysDrawsZero);
    }

    private void fillIDSpaceExceptFor(int freeID) {
        for (int ID = 0; ID < ID_SPACE; ID++) {
            if (ID != freeID) {
                persistentData.addMailbox(new Mailbox(logger, ID, UUID.randomUUID()));
            }
        }
    }

    @Test
    public void testCreateMailboxFallsBackToAFreeIDWhenRandomDrawsKeepColliding() {
        int freeID = 2;
        fillIDSpaceExceptFor(freeID);

        Mailbox mailbox = mailboxFactoryAlwaysDrawingTakenID().createMailbox(player);

        assertNotNull("A free ID was available, so a mailbox should have been created", mailbox);
        assertEquals("The one free ID should have been used instead of the repeatedly drawn taken one", freeID, mailbox.getID());
    }

    @Test
    public void testCreateMailboxReturnsNullWhenEveryIDIsTaken() {
        fillIDSpaceExceptFor(-1);

        assertNull("A saturated ID space should refuse creation rather than reuse an ID",
                mailboxFactoryAlwaysDrawingTakenID().createMailbox(player));
        verify(logger).logError(contains("in use"));
    }

    @Test
    public void testCreateMailboxUsesAFreeRandomDraw() {
        Random alwaysDrawsOne = new Random() {
            @Override
            public int nextInt(int bound) {
                return 1;
            }
        };

        Mailbox mailbox = new MailboxFactory(configService, persistentData, logger, alwaysDrawsOne).createMailbox(player);

        assertEquals(1, mailbox.getID());
    }

    @Test
    public void testCreatedMailboxIsOwnedByThePlayer() {
        Mailbox mailbox = new MailboxFactory(configService, persistentData, logger).createMailbox(player);

        assertEquals(playerUUID, mailbox.getOwnerUUID());
    }
}
