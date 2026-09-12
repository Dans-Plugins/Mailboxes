package dansplugins.mailboxes.services;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {

    @Mock
    private FileConfiguration config;

    @Mock
    private CommandSender sender;

    private ConfigService configService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mailboxes is final and so cannot be mocked; the two members ConfigService reads from it
        // are overridden here so that a config change can be exercised without a live plugin.
        configService = new ConfigService(null) {
            @Override
            public FileConfiguration getConfig() {
                return config;
            }

            @Override
            void saveConfig() {
            }
        };
    }

    @Test
    public void testSetIntegerOptionStoresAValidValue() {
        when(config.isSet("maxMessageIDNumber")).thenReturn(true);

        configService.setConfigOption("maxMessageIDNumber", "500", sender);

        verify(config).set("maxMessageIDNumber", 500);
        verify(sender).sendMessage(contains("Integer set."));
        assertTrue(configService.hasBeenAltered());
    }

    @Test
    public void testSetIntegerOptionRejectsZero() {
        when(config.isSet("maxMessageIDNumber")).thenReturn(true);

        // Zero is the value that makes MessageFactory's Random.nextInt throw on every attempt to
        // create a message, so accepting it disables message creation server-wide.
        configService.setConfigOption("maxMessageIDNumber", "0", sender);

        verify(config, never()).set(eq("maxMessageIDNumber"), any());
        verify(sender).sendMessage(contains("maxMessageIDNumber must be at least 1."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetIntegerOptionRejectsANegativeValue() {
        when(config.isSet("maxMailboxIDNumber")).thenReturn(true);

        configService.setConfigOption("maxMailboxIDNumber", "-25", sender);

        verify(config, never()).set(eq("maxMailboxIDNumber"), any());
        verify(sender).sendMessage(contains("maxMailboxIDNumber must be at least 1."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetIntegerOptionRejectsANonNumericValue() {
        when(config.isSet("maxAttachmentStackSize")).thenReturn(true);

        // Integer.parseInt used to throw out of the command, so the sender saw a stack trace
        // rather than an explanation.
        configService.setConfigOption("maxAttachmentStackSize", "sixty-four", sender);

        verify(config, never()).set(eq("maxAttachmentStackSize"), any());
        verify(sender).sendMessage(contains("The value given for maxAttachmentStackSize must be a whole number."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetIntegerOptionRejectsAValueTooLargeForAnInteger() {
        when(config.isSet("maxMessageIDNumber")).thenReturn(true);

        configService.setConfigOption("maxMessageIDNumber", "99999999999", sender);

        verify(config, never()).set(eq("maxMessageIDNumber"), any());
        verify(sender).sendMessage(contains("The value given for maxMessageIDNumber must be a whole number."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetBooleanOptionIsUnaffectedByIntegerValidation() {
        when(config.isSet("debugMode")).thenReturn(true);

        configService.setConfigOption("debugMode", "true", sender);

        verify(config).set("debugMode", true);
        verify(sender).sendMessage(contains("Boolean set."));
    }

    @Test
    public void testSetVersionIsRefused() {
        when(config.isSet("version")).thenReturn(true);

        configService.setConfigOption("version", "9.9.9", sender);

        verify(config, never()).set(eq("version"), any());
        verify(sender).sendMessage(contains("Cannot set version."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetUnknownOptionIsRefused() {
        when(config.isSet("madeUpOption")).thenReturn(false);

        configService.setConfigOption("madeUpOption", "1", sender);

        verify(config, never()).set(eq("madeUpOption"), any());
        verify(sender).sendMessage(contains("That config option wasn't found."));
        assertFalse(configService.hasBeenAltered());
    }

    // The usage-reporting getters must use the one-argument FileConfiguration
    // calls: a config.yml written before the block existed is never rewritten,
    // and only the one-argument getters fall through to the jar's bundled
    // defaults. The two-argument forms would return their fallback instead and
    // turn reporting off on every existing installation.

    @Test
    public void testUsageReportingEnabledIsReadWithTheOneArgumentGetter() {
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(true);

        assertTrue(configService.isUsageReportingEnabled());

        verify(config).getBoolean("usage-reporting.enabled");
        verify(config, never()).getBoolean(eq("usage-reporting.enabled"), anyBoolean());
    }

    @Test
    public void testUsageReportingEndpointIsReadWithTheOneArgumentGetter() {
        when(config.getString("usage-reporting.endpoint")).thenReturn("http://localhost:8080");

        assertEquals("http://localhost:8080", configService.getUsageReportingEndpoint());

        verify(config).getString("usage-reporting.endpoint");
        verify(config, never()).getString(eq("usage-reporting.endpoint"), anyString());
    }

    @Test
    public void testUsageReportingEndpointFallsBackToTheTraceServerWhenAbsent() {
        when(config.getString("usage-reporting.endpoint")).thenReturn(null);

        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
    }

    @Test
    public void testUsageReportingKeyIsReadWithTheOneArgumentGetter() {
        when(config.getString("usage-reporting.key")).thenReturn("abc");

        assertEquals("abc", configService.getUsageReportingKey());

        verify(config).getString("usage-reporting.key");
        verify(config, never()).getString(eq("usage-reporting.key"), anyString());
    }

    @Test
    public void testUsageReportingKeyIsEmptyWhenAbsent() {
        when(config.getString("usage-reporting.key")).thenReturn(null);

        assertEquals("", configService.getUsageReportingKey());
    }
}
