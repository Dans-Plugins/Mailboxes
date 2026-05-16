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
| `assignmentAlertEnabled` | Boolean | `false` | Whether players are notified when a message is sent to them. |
| `unreadMessagesAlertEnabled` | Boolean | `true` | Whether players are reminded of unread messages on join. |
| `welcomeMessageEnabled` | Boolean | `true` | Whether a welcome message is shown to players on join. |
| `quotesEnabled` | Boolean | `false` | Whether random quotes are displayed to players. |
| `attachmentsEnabled` | Boolean | `true` | Whether item attachments are enabled for messages. |
