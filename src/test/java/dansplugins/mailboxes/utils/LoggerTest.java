package dansplugins.mailboxes.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Only {@link Logger#logError(String)} is covered here: {@link Logger#log(String)} consults
 * {@code Mailboxes#isDebugEnabled()}, and {@code Mailboxes} is a final class that Mockito 3.12.4
 * (without the inline mock maker) cannot mock.
 */
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
    public void testLogErrorPrintsPrefixedMessageWithoutConsultingDebugMode() {
        // A null plugin would throw if logError checked isDebugEnabled() the way log() does.
        Logger logger = new Logger(null);

        logger.logError("could not write mailboxes.json");

        assertEquals("[Mailboxes] [ERROR] could not write mailboxes.json" + System.lineSeparator(), capturedOut.toString());
    }
}
