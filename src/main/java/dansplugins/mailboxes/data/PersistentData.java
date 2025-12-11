package dansplugins.mailboxes.data;

import dansplugins.mailboxes.objects.Mailbox;
import dansplugins.mailboxes.objects.Message;
import dansplugins.mailboxes.objects.TopicMailbox;
import dansplugins.mailboxes.objects.TopicMessage;
import dansplugins.mailboxes.utils.Logger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class PersistentData {
    private final LookupService lookupService;

    private final ArrayList<Mailbox> mailboxes = new ArrayList<>();
    private final ArrayList<TopicMailbox> topicMailboxes = new ArrayList<>();
    private final ArrayList<TopicMessage> topicMessages = new ArrayList<>();

    public PersistentData(Logger logger) {
        this.lookupService = new LookupService(logger, this);
    }

    public ArrayList<Mailbox> getMailboxes() {
        return mailboxes;
    }

    public Mailbox getMailbox(Player player) {
        return lookupService.lookup(player.getUniqueId());
    }

    public Mailbox getMailbox(UUID playerUUID) {
        for (Mailbox mailbox : mailboxes) {
            if (mailbox.getOwnerUUID().equals(playerUUID)) {
                return mailbox;
            }
        }
        return null;
    }

    public Mailbox getMailbox(int mailboxID) {
        for (Mailbox mailbox : mailboxes) {
            if (mailbox.getID() == mailboxID) {
                return mailbox;
            }
        }
        return null;
    }

    public void addMailbox(Mailbox mailbox) {
        if (getMailbox(mailbox.getOwnerUUID()) == null) {
            mailboxes.add(mailbox);
        }
    }

    public void removeMailbox(Mailbox mailbox) {
        mailboxes.remove(mailbox);
    }

    public Message getMessage(int messageID) {
        for (Mailbox mailbox : mailboxes) {
            for (Message message : mailbox.getActiveMessages()) {
                if (message.getID() == messageID) {
                    return message;
                }
            }
        }
        return null;
    }

    public LookupService getLookupService() {
        return lookupService;
    }

    // Topic Mailbox methods
    public ArrayList<TopicMailbox> getTopicMailboxes() {
        return topicMailboxes;
    }

    public TopicMailbox getTopicMailbox(String name) {
        for (TopicMailbox topicMailbox : topicMailboxes) {
            if (topicMailbox.getName().equals(name)) {
                return topicMailbox;
            }
        }
        return null;
    }

    public TopicMailbox getTopicMailbox(int id) {
        for (TopicMailbox topicMailbox : topicMailboxes) {
            if (topicMailbox.getID() == id) {
                return topicMailbox;
            }
        }
        return null;
    }

    public void addTopicMailbox(TopicMailbox topicMailbox) {
        if (getTopicMailbox(topicMailbox.getName()) == null) {
            topicMailboxes.add(topicMailbox);
        }
    }

    public void removeTopicMailbox(TopicMailbox topicMailbox) {
        topicMailboxes.remove(topicMailbox);
    }

    // Topic Message methods
    public ArrayList<TopicMessage> getTopicMessages() {
        return topicMessages;
    }

    public TopicMessage getTopicMessage(int id) {
        for (TopicMessage message : topicMessages) {
            if (message.getID() == id) {
                return message;
            }
        }
        return null;
    }

    public void addTopicMessage(TopicMessage message) {
        topicMessages.add(message);
    }

    public void removeTopicMessage(TopicMessage message) {
        topicMessages.remove(message);
    }

    public class LookupService {
        private final Logger logger;
        private final PersistentData persistentData;

        private final HashSet<Mailbox> cache = new HashSet<>();

        public LookupService(Logger logger, PersistentData persistentData) {
            this.logger = logger;
            this.persistentData = persistentData;
        }

        public Mailbox lookup(UUID playerUUID) {
            logger.log("Looking up mailbox for " + playerUUID.toString());
            Mailbox mailbox = checkCache(playerUUID);
            if (mailbox == null) {
                return checkStorage(playerUUID);
            }
            return mailbox;
        }

        private Mailbox checkCache(UUID playerUUID) {
            for (Mailbox mailbox : cache) {
                if (mailbox.getOwnerUUID().equals(playerUUID)) {
                    logger.log("Found in cache!");
                    return mailbox;
                }
            }
            return null;
        }

        private Mailbox checkStorage(UUID playerUUID) {
            Mailbox mailbox = persistentData.getMailbox(playerUUID);
            if (mailbox != null) {
                logger.log("Found in storage!");
                cache.add(mailbox);
            }
            else {
                logger.log("Not found.");
            }
            return mailbox;
        }

    }
}
