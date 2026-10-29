package dansplugins.mailboxes.utils;

import dansplugins.mailboxes.services.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SchedulerTest {

    private static final BukkitScheduler bukkitScheduler = MockServer.SCHEDULER;

    @Mock
    private StorageService storageService;

    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;
    private Scheduler scheduler;

    @BeforeClass
    public static void installServer() {
        MockServer.install();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reset(bukkitScheduler);
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true));
        // Mailboxes is final and cannot be mocked; the mock scheduler accepts a null plugin.
        scheduler = new Scheduler(new Logger(() -> true), null, storageService);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void testScheduleAutosaveRepeatsHourlyAfterAnHour() {
        assumeBukkitSchedulerIsOurs();

        scheduler.scheduleAutosave();

        verify(bukkitScheduler).scheduleSyncRepeatingTask(isNull(), any(Runnable.class), eq(72000L), eq(72000L));
        verify(storageService, never()).save();
    }

    @Test
    public void testScheduledTaskSavesStorage() {
        assumeBukkitSchedulerIsOurs();

        scheduler.scheduleAutosave();
        captureScheduledTask().run();

        verify(storageService).save();
    }

    @Test
    public void testLogLinesCarryOnlyTheLoggerPrefix() {
        assumeBukkitSchedulerIsOurs();

        scheduler.scheduleAutosave();
        captureScheduledTask().run();

        String newline = System.lineSeparator();
        assertEquals(
                "[Mailboxes] Scheduling hourly autosave." + newline
                        + "[Mailboxes] Saving. This will happen hourly." + newline,
                capturedOut.toString());
    }

    private Runnable captureScheduledTask() {
        ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).scheduleSyncRepeatingTask(isNull(), task.capture(), anyLong(), anyLong());
        return task.getValue();
    }

    private void assumeBukkitSchedulerIsOurs() {
        org.junit.Assume.assumeTrue("another test installed a different Bukkit server",
                Bukkit.getScheduler() == bukkitScheduler);
    }
}
