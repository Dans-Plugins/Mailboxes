package dansplugins.mailboxes.services;

import dansplugins.mailboxes.data.PersistentData;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MailServiceTest {

    @Mock
    private Logger logger;

    @Mock
    private PersistentData.LookupService lookupService;

    @Mock
    private Message message;

    private MailService mailService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Mailboxes is final and so cannot be mocked; MailService only consults it from
        // sendWelcomeMessage, which these tests do not exercise.
        mailService = new MailService(null, logger, lookupService);
    }

    @Test
    public void testSendMessageWithNullMessage() {
        // MessageFactory returns null when no free message ID is available, so this is a
        // reachable state rather than a defensive one.
        assertFalse(mailService.sendMessage(null));
        verifyNoInteractions(lookupService);
    }

    @Test
    public void testSendMessageWithAMessageOfNoKnownType() {
        assertFalse(mailService.sendMessage(message));
    }
}
