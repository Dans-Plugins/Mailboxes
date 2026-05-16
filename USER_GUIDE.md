# Mailboxes User Guide

## What is Mailboxes?

Mailboxes is a Spigot plugin that gives players an in-game messaging system. Players can send text messages and attach items to other players, even when they are offline. Messages are stored persistently and can be listed, opened, archived, or deleted.

## Installation

1. Download the latest `Mailboxes-<version>.jar` from the [Releases](https://github.com/Dans-Plugins/Mailboxes/releases) page.
2. Place the JAR in your server's `plugins/` folder.
3. Restart the server.

## Getting Started

1. Send a message: `/m send <player> "Hello!"`
2. Attach an item to a message: hold the item and add `-attach`: `/m send <player> "Here is a gift" -attach`
3. List your messages: `/m list`
4. Open a message: `/m open`
5. Archive or delete messages you no longer need.

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `mailboxes.help` | `true` | View the help menu. |
| `mailboxes.list` | `true` | List messages. |
| `mailboxes.open` | `true` | Open a message. |
| `mailboxes.send` | `true` | Send a text message. |
| `mailboxes.send.attach` | `true` | Send a message with an item attached. |
| `mailboxes.delete` | `true` | Delete a message. |
| `mailboxes.archive` | `true` | Archive a message. |
| `mailboxes.config` | `op` | View or change config options. |

## Support

Ask questions in the [Discord server](https://discord.gg/xXtuAQ2) or open a [GitHub issue](https://github.com/Dans-Plugins/Mailboxes/issues).
