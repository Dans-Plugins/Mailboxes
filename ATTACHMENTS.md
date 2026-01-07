# Item Attachments Feature

## Overview
The Mailboxes plugin now supports attaching items to messages, allowing players to easily transfer items to other players through in-game mail.

## Usage

### Sending a Message with Attachments
To send a message with an item attachment:

1. Hold the item you want to attach in your main hand
2. Use the command: `/m send <playerName> "message" -attach`

Example:
```
/m send John "Here's that diamond sword I promised you!" -attach
```

### Receiving Messages with Attachments
When you open a message with attachments:

1. The message will display the attached items
2. Items will automatically be added to your inventory
3. If your inventory is full, the items will remain attached to the message until you have space

### Viewing Messages with Attachments
Messages with attachments are marked with a 📎 icon in the message list.

## Configuration

The following configuration options are available in `config.yml`:

- **attachmentsEnabled**: Enable or disable the attachments feature (default: `true`)
- **maxAttachmentStackSize**: Maximum stack size for each attachment (default: `64`)

Note: Currently, only one item can be attached per message.

### Configuration Example
```yaml
attachmentsEnabled: true
maxAttachmentStackSize: 64
```

## Permissions

- **mailboxes.send.attach**: Allows players to send messages with attachments (default: `true`)

## Features

- **Secure Transfer**: Items are stored securely with the message and delivered to the recipient
- **Inventory Management**: Handles full inventory scenarios gracefully
- **Visual Indicators**: Shows attachment information in message lists and when opening messages
- **Configurable Limits**: Server admins can configure max attachments and stack sizes
- **Persistent Storage**: Attachments are saved and persist through server restarts

## Technical Details

- Attachments are serialized using Bukkit's ItemStack serialization
- Items are stored as part of the message data
- Attachments are cleared from messages after successful delivery
- If inventory is full, items remain attached until the player has space
