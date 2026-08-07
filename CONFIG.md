# Mailboxes Configuration

Configuration can be viewed and changed in-game with `/m config show` and `/m config set <option> <value>`. A `config.yml` is generated in `plugins/Mailboxes/` on first run.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `version` | String | *(plugin version)* | Plugin version. Do not edit manually. |
| `debugMode` | Boolean | `false` | Enables verbose debug logging to the console. |
| `maxMessageIDNumber` | Integer | `10000` | Maximum ID number assigned to messages. |
| `maxMailboxIDNumber` | Integer | `10000` | Maximum ID number assigned to mailboxes. |
| `maxAttachmentStackSize` | Integer | `64` | Maximum stack size allowed for item attachments. |
| `preventSendingMessagesToSelf` | Boolean | `true` | Whether players can send messages to themselves. |
| `assignmentAlertEnabled` | Boolean | `false` | Whether a player is told "You have been assigned a mailbox" the first time a mailbox is created for them. |
| `unreadMessagesAlertEnabled` | Boolean | `true` | Whether players are reminded of unread messages on join. |
| `welcomeMessageEnabled` | Boolean | `true` | Whether a welcome message is delivered to a player's mailbox the first time one is created for them. |
| `quotesEnabled` | Boolean | `false` | Whether message content is wrapped in double quotes when a message is opened. |
| `attachmentsEnabled` | Boolean | `true` | Whether item attachments are enabled for messages. |
