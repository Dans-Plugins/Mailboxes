package dansplugins.mailboxes.factories;

import dansplugins.mailboxes.objects.Message;

public class MessageFactory {

    private static MessageFactory instance;

    private MessageFactory() {

    }

    public static MessageFactory getInstance() {
        if (instance == null) {
            instance = new MessageFactory();
        }
        return instance;
    }

    public Message createMessage(String type, String sender, String recipient, String content) {
        int ID = getNewID();
        return new Message(ID, type, sender, recipient, content);
    }

    private int getNewID() {
        // TODO: implement
        return -1;
    }

}
