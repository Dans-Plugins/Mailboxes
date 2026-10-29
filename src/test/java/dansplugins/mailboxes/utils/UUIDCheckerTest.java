package dansplugins.mailboxes.utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.*;

public class UUIDCheckerTest {

    private UUIDChecker uuidChecker;

    @Before
    public void setUp() {
        assumeTrue("another test installed a different Bukkit server", MockServer.install());
        setOnlinePlayers();
        setOfflinePlayers();
        uuidChecker = new UUIDChecker();
    }

    @Test
    public void testFindPlayerNameBasedOnUUIDReturnsOnlinePlayersName() {
        UUID uuid = UUID.randomUUID();
        setOnlinePlayers(onlinePlayer("Alice", uuid));

        assertEquals("Alice", uuidChecker.findPlayerNameBasedOnUUID(uuid));
    }

    @Test
    public void testFindPlayerNameBasedOnUUIDFallsBackToOfflinePlayers() {
        UUID uuid = UUID.randomUUID();
        setOnlinePlayers(onlinePlayer("Alice", UUID.randomUUID()));
        setOfflinePlayers(offlinePlayer("Bob", uuid));

        assertEquals("Bob", uuidChecker.findPlayerNameBasedOnUUID(uuid));
    }

    @Test
    public void testFindPlayerNameBasedOnUUIDReturnsEmptyStringForUnknownUUID() {
        setOnlinePlayers(onlinePlayer("Alice", UUID.randomUUID()));
        setOfflinePlayers(offlinePlayer("Bob", UUID.randomUUID()));

        assertEquals("", uuidChecker.findPlayerNameBasedOnUUID(UUID.randomUUID()));
    }

    @Test
    public void testFindPlayerNameBasedOnUUIDReturnsNullForOfflinePlayerWithoutRecordedName() {
        UUID uuid = UUID.randomUUID();
        setOfflinePlayers(offlinePlayer(null, uuid));

        assertNull(uuidChecker.findPlayerNameBasedOnUUID(uuid));
    }

    @Test
    public void testFindUUIDBasedOnPlayerNameIgnoresCase() {
        UUID uuid = UUID.randomUUID();
        setOnlinePlayers(onlinePlayer("Alice", uuid));

        assertEquals(uuid, uuidChecker.findUUIDBasedOnPlayerName("aLICE"));
    }

    @Test
    public void testFindUUIDBasedOnPlayerNamePrefersOnlinePlayer() {
        UUID onlineUUID = UUID.randomUUID();
        setOnlinePlayers(onlinePlayer("Alice", onlineUUID));
        setOfflinePlayers(offlinePlayer("Alice", UUID.randomUUID()));

        assertEquals(onlineUUID, uuidChecker.findUUIDBasedOnPlayerName("Alice"));
    }

    @Test
    public void testFindUUIDBasedOnPlayerNameSkipsOfflinePlayersWithoutRecordedName() {
        UUID uuid = UUID.randomUUID();
        setOfflinePlayers(offlinePlayer(null, UUID.randomUUID()), offlinePlayer("Bob", uuid));

        assertEquals(uuid, uuidChecker.findUUIDBasedOnPlayerName("Bob"));
    }

    @Test
    public void testFindUUIDBasedOnPlayerNameReturnsNullForUnknownName() {
        setOnlinePlayers(onlinePlayer("Alice", UUID.randomUUID()));
        setOfflinePlayers(offlinePlayer("Bob", UUID.randomUUID()));

        assertNull(uuidChecker.findUUIDBasedOnPlayerName("Carol"));
    }

    private Player onlinePlayer(String name, UUID uuid) {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        when(player.getUniqueId()).thenReturn(uuid);
        return player;
    }

    private OfflinePlayer offlinePlayer(String name, UUID uuid) {
        OfflinePlayer player = mock(OfflinePlayer.class);
        when(player.getName()).thenReturn(name);
        when(player.getUniqueId()).thenReturn(uuid);
        return player;
    }

    private void setOnlinePlayers(Player... players) {
        doReturn(Arrays.asList(players)).when(MockServer.SERVER).getOnlinePlayers();
    }

    private void setOfflinePlayers(OfflinePlayer... players) {
        doReturn(players).when(MockServer.SERVER).getOfflinePlayers();
    }
}
