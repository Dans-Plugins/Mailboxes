# Mailboxes Commands

All commands use `/m` or `/mailboxes` as the base.

| Command | Description | Permission |
|---------|-------------|------------|
| `/m help` | View a list of commands. | `mailboxes.help` |
| `/m list [type] [page]` | List your messages with optional type filter and pagination. `type` is one of `active` (the default), `archived`, or `unread`. | `mailboxes.list` |
| `/m open <ID>` | Open the message with the given ID, marking it read and delivering any attachments. | `mailboxes.open` |
| `/m send <player> "<message>"` | Send a text message to a player. The message must be surrounded by double quotes. | `mailboxes.send` |
| `/m send <player> "<message>" -attach` | Send a message with the item in your hand attached. | `mailboxes.send.attach` |
| `/m delete <ID>` | Delete the message with the given ID. | `mailboxes.delete` |
| `/m archive <ID>` | Archive the message with the given ID. | `mailboxes.archive` |
| `/m config show` | View the current config options (operators). | `mailboxes.config` |
| `/m config set <option> <value>` | Set a config option (operators). | `mailboxes.config` |
| `/m stats` | View the total, active, archived, and unread message counts for your mailbox. | `mailboxes.stats` |
