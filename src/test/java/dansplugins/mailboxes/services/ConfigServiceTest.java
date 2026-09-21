package dansplugins.mailboxes.services;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConfigServiceTest {

    @Mock
    private FileConfiguration config;

    @Mock
    private CommandSender sender;

    private ConfigService configService;

    private final List<String> warnings = new ArrayList<>();
    private int saveCount = 0;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mailboxes is final and so cannot be mocked; the members ConfigService reads from it
        // are overridden here so that a config change can be exercised without a live plugin.
        configService = new ConfigService(null) {
            @Override
            public FileConfiguration getConfig() {
                return config;
            }

            @Override
            void saveConfig() {
                saveCount++;
            }

            @Override
            void logWarning(String message) {
                warnings.add(message);
            }
        };
    }

    /** Stubs the mock to hold an integer for the option, as YamlConfiguration would after loading it. */
    private void givenLoadedInteger(String option, int value) {
        when(config.isSet(option)).thenReturn(true);
        when(config.isInt(option)).thenReturn(true);
        when(config.getInt(option)).thenReturn(value);
        when(config.get(option)).thenReturn(value);
    }

    /** Stubs the mock to hold a value that is not an integer for the option (a string, say). */
    private void givenLoadedNonInteger(String option, Object value) {
        when(config.isSet(option)).thenReturn(true);
        when(config.isInt(option)).thenReturn(false);
        // FileConfiguration.getInt returns 0 for a value it cannot coerce, which is the value
        // that reaches Random.nextInt if the option is not caught here.
        when(config.getInt(option)).thenReturn(0);
        when(config.get(option)).thenReturn(value);
    }

    private void givenLoadedBoolean(String option, boolean value) {
        when(config.isSet(option)).thenReturn(true);
        when(config.isBoolean(option)).thenReturn(true);
        when(config.getBoolean(option)).thenReturn(value);
        when(config.get(option)).thenReturn(value);
    }

    private void givenLoadedNonBoolean(String option, Object value) {
        when(config.isSet(option)).thenReturn(true);
        when(config.isBoolean(option)).thenReturn(false);
        when(config.get(option)).thenReturn(value);
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
    public void testSetBooleanOptionStoresFalse() {
        when(config.isSet("attachmentsEnabled")).thenReturn(true);

        configService.setConfigOption("attachmentsEnabled", "false", sender);

        verify(config).set("attachmentsEnabled", false);
        verify(sender).sendMessage(contains("Boolean set."));
        assertTrue(configService.hasBeenAltered());
    }

    @Test
    public void testSetBooleanOptionAcceptsTrueInAnyCase() {
        when(config.isSet("quotesEnabled")).thenReturn(true);

        configService.setConfigOption("quotesEnabled", "TRUE", sender);

        verify(config).set("quotesEnabled", true);
        verify(sender).sendMessage(contains("Boolean set."));
    }

    @Test
    public void testSetBooleanOptionRejectsYes() {
        when(config.isSet("attachmentsEnabled")).thenReturn(true);

        // Boolean.parseBoolean("yes") is false, so this used to disable attachments under a
        // success message when the sender meant to enable them.
        configService.setConfigOption("attachmentsEnabled", "yes", sender);

        verify(config, never()).set(eq("attachmentsEnabled"), any());
        verify(sender).sendMessage(contains("The value given for attachmentsEnabled must be true or false."));
        verify(sender, never()).sendMessage(contains("Boolean set."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetBooleanOptionRejectsANumber() {
        when(config.isSet("debugMode")).thenReturn(true);

        configService.setConfigOption("debugMode", "1", sender);

        verify(config, never()).set(eq("debugMode"), any());
        verify(sender).sendMessage(contains("The value given for debugMode must be true or false."));
        assertFalse(configService.hasBeenAltered());
    }

    @Test
    public void testSetBooleanOptionRejectsAMisspelling() {
        when(config.isSet("quotesEnabled")).thenReturn(true);

        configService.setConfigOption("quotesEnabled", "ture", sender);

        verify(config, never()).set(eq("quotesEnabled"), any());
        verify(sender).sendMessage(contains("The value given for quotesEnabled must be true or false."));
        assertFalse(configService.hasBeenAltered());
    }

    // Values loaded from config.yml are checked on every enable, because the /m config set
    // validation above cannot reach a file edited by hand or written before it existed.

    @Test
    public void testReplaceUnusableConfigValuesLeavesUsableValuesAlone() {
        givenLoadedInteger("maxMessageIDNumber", 10000);
        givenLoadedInteger("maxMailboxIDNumber", 1);
        givenLoadedInteger("maxAttachmentStackSize", 64);
        givenLoadedBoolean("debugMode", true);
        givenLoadedBoolean("attachmentsEnabled", false);

        configService.replaceUnusableConfigValues();

        verify(config, never()).set(anyString(), any());
        assertTrue(warnings.isEmpty());
        assertEquals(0, saveCount);
    }

    @Test
    public void testReplaceUnusableConfigValuesSkipsAbsentOptions() {
        // Nothing is stubbed, so every isSet call answers false: a file the defaults have not
        // yet been written to is not the concern of this check.
        configService.replaceUnusableConfigValues();

        verify(config, never()).set(anyString(), any());
        assertTrue(warnings.isEmpty());
        assertEquals(0, saveCount);
    }

    @Test
    public void testReplaceUnusableConfigValuesReplacesAZeroMaxMessageIDNumber() {
        // The value that makes every message creation throw out of Random.nextInt.
        givenLoadedInteger("maxMessageIDNumber", 0);

        configService.replaceUnusableConfigValues();

        verify(config).set("maxMessageIDNumber", 10000);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0), warnings.get(0).contains("maxMessageIDNumber is set to 0 in config.yml"));
        assertTrue(warnings.get(0), warnings.get(0).contains("the default of 10000 has been used instead"));
        assertEquals(1, saveCount);
    }

    @Test
    public void testReplaceUnusableConfigValuesReplacesANegativeMaxMailboxIDNumber() {
        givenLoadedInteger("maxMailboxIDNumber", -5);

        configService.replaceUnusableConfigValues();

        verify(config).set("maxMailboxIDNumber", 10000);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0), warnings.get(0).contains("maxMailboxIDNumber is set to -5 in config.yml"));
    }

    @Test
    public void testReplaceUnusableConfigValuesReplacesANonIntegerMaxAttachmentStackSize() {
        givenLoadedNonInteger("maxAttachmentStackSize", "sixty-four");

        configService.replaceUnusableConfigValues();

        verify(config).set("maxAttachmentStackSize", 64);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0), warnings.get(0).contains("maxAttachmentStackSize is set to sixty-four in config.yml"));
        assertTrue(warnings.get(0), warnings.get(0).contains("the default of 64 has been used instead"));
    }

    @Test
    public void testReplaceUnusableConfigValuesReplacesANonBooleanOption() {
        givenLoadedNonBoolean("attachmentsEnabled", "yes please");

        configService.replaceUnusableConfigValues();

        verify(config).set("attachmentsEnabled", true);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0), warnings.get(0).contains("attachmentsEnabled is set to yes please in config.yml"));
        assertTrue(warnings.get(0), warnings.get(0).contains("the default of true has been used instead"));
        assertEquals(1, saveCount);
    }

    @Test
    public void testReplaceUnusableConfigValuesReportsEveryUnusableValueAndSavesOnce() {
        givenLoadedInteger("maxMessageIDNumber", 0);
        givenLoadedNonInteger("maxMailboxIDNumber", 2.5);
        givenLoadedInteger("maxAttachmentStackSize", 64);
        givenLoadedNonBoolean("debugMode", "on");

        configService.replaceUnusableConfigValues();

        verify(config).set("maxMessageIDNumber", 10000);
        verify(config).set("maxMailboxIDNumber", 10000);
        verify(config, never()).set(eq("maxAttachmentStackSize"), any());
        verify(config).set("debugMode", false);
        assertEquals(3, warnings.size());
        assertEquals(1, saveCount);
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
