# Mailboxes Configuration

Configuration can be viewed and changed in-game with `/m config show` and `/m config set <option> <value>`. A `config.yml` is generated in `plugins/Mailboxes/` on first run.

Integer options are validated before being stored: a value that is not a whole number, or one below `1`, is rejected with an explanation and the option is left unchanged. Values in `config.yml` are not validated — a `config.yml` edited by hand is read as-is.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `version` | String | *(plugin version)* | Plugin version. Do not edit manually. |
| `debugMode` | Boolean | `false` | Enables verbose debug logging to the console. |
| `maxMessageIDNumber` | Integer | `10000` | Maximum ID number assigned to messages. This also caps how many messages can exist at once: once every ID below it is in use, no further message can be created until messages are deleted or the limit is raised. |
| `maxMailboxIDNumber` | Integer | `10000` | Maximum ID number assigned to mailboxes. |
| `maxAttachmentStackSize` | Integer | `64` | Maximum stack size allowed for item attachments. |
| `preventSendingMessagesToSelf` | Boolean | `true` | Whether players can send messages to themselves. |
| `assignmentAlertEnabled` | Boolean | `false` | Whether a player is told "You have been assigned a mailbox" the first time a mailbox is created for them. |
| `unreadMessagesAlertEnabled` | Boolean | `true` | Whether players are reminded of unread messages on join. |
| `welcomeMessageEnabled` | Boolean | `true` | Whether a welcome message is delivered to a player's mailbox the first time one is created for them. |
| `quotesEnabled` | Boolean | `false` | Whether message content is wrapped in double quotes when a message is opened. |
| `attachmentsEnabled` | Boolean | `true` | Whether item attachments are enabled for messages. |
| `usage-reporting.enabled` | Boolean | `true` | Whether the plugin reports usage events (see below). Set to `false` to turn it off. |
| `usage-reporting.endpoint` | String | `https://trace.danielstephenson.dev` | The trace server events are sent to. |
| `usage-reporting.key` | String | the plugin's key | Identifies this plugin to the trace server so reports are attributed to it. Not a secret: it ships in the default config and can only report as Mailboxes. Empty means reporting is off regardless of `enabled`. |

The `usage-reporting` options are read from `config.yml` only and cannot be changed with `/m config set`. A `config.yml` written by a version of the plugin from before the block existed is not rewritten; the bundled defaults are used for any key the file lacks, so reporting is active on upgraded servers unless it is turned off.

## Usage reporting

When the plugin is enabled, and each time one of its commands is used, a small event is sent to the
author's [trace](https://github.com/Stephenson-Software/trace-client-java) server so it is known which
plugins are actually in use. An event carries the plugin's name, the event name (`startup` or
`command`), and either the plugin version or the command name — nothing about players, the world, or
the server. Sending happens off the main thread, never delays a tick, and is dropped silently if the
server cannot be reached. Set `usage-reporting.enabled` to `false` to turn it off.
