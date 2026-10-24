package dansplugins.mailboxes.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class LoggerTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @Before
    public void setUp() {
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testLogPrintsPrefixedMessageWhenDebugModeIsEnabled() {
        Logger logger = new Logger(() -> true);

        logger.log("loaded 3 mailboxes");

        assertEquals("[Mailboxes] loaded 3 mailboxes" + System.lineSeparator(), capturedOut.toString());
    }

    @Test
    public void testLogPrintsNothingWhenDebugModeIsDisabled() {
        Logger logger = new Logger(() -> false);

        logger.log("loaded 3 mailboxes");

        assertEquals("", capturedOut.toString());
    }

    @Test
    public void testLogConsultsDebugModeOnEveryCall() {
        AtomicBoolean debugMode = new AtomicBoolean(false);
        Logger logger = new Logger(debugMode::get);

        logger.log("before toggle");
        debugMode.set(true);
        logger.log("after toggle");

        assertEquals("[Mailboxes] after toggle" + System.lineSeparator(), capturedOut.toString());
    }

    @Test
    public void testLogErrorPrintsPrefixedMessageWithoutConsultingDebugMode() {
        Logger logger = new Logger(() -> {
            throw new AssertionError("logError must not consult debug mode");
        });

        logger.logError("could not write mailboxes.json");

        assertEquals("[Mailboxes] [ERROR] could not write mailboxes.json" + System.lineSeparator(), capturedOut.toString());
    }
}
