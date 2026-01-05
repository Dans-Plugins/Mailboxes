# Mailboxes API Documentation

This document provides comprehensive documentation for plugin developers who want to integrate with the Mailboxes plugin.

## Table of Contents
- [Getting Started](#getting-started)
- [Setup](#setup)
- [Accessing the API](#accessing-the-api)
- [API Reference](#api-reference)
- [Examples](#examples)
- [Best Practices](#best-practices)

## Getting Started

The Mailboxes API allows your plugin to send persistent messages to players programmatically. This is useful for notifications, rewards, alerts, or any other plugin-to-player communication needs.

### Key Features
- Send messages from your plugin to any player
- Messages persist across server restarts
- Messages are delivered even if the player is offline
- Support for item attachments (optional)
- Access player mailboxes and messages

## Setup

### Adding Mailboxes as a Dependency

#### Option 1: Maven (Recommended)

First, add the Mailboxes JAR to your local Maven repository or use a local file dependency:

**Using a local file dependency:**
```xml
<dependencies>
    <dependency>
        <groupId>dansplugins</groupId>
        <artifactId>Mailboxes</artifactId>
        <version>1.2.0</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/libs/Mailboxes-1.2.0.jar</systemPath>
    </dependency>
</dependencies>
```

**Or install to local Maven repository:**
```bash
mvn install:install-file \
  -Dfile=Mailboxes-1.2.0.jar \
  -DgroupId=dansplugins \
  -DartifactId=Mailboxes \
  -Dversion=1.2.0 \
  -Dpackaging=jar
```

Then add to your `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>dansplugins</groupId>
        <artifactId>Mailboxes</artifactId>
        <version>1.2.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Option 2: Gradle

```groovy
dependencies {
    compileOnly files('libs/Mailboxes-1.2.0.jar')
}
```

### Declaring the Dependency

Add Mailboxes as a dependency in your `plugin.yml`:

```yaml
name: YourPlugin
version: 1.0
main: com.example.yourplugin.YourPlugin
depend: [Mailboxes]
```

Or as a soft dependency if Mailboxes is optional:

```yaml
softdepend: [Mailboxes]
```

## Accessing the API

### Getting the API Instance

```java
import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import org.bukkit.plugin.Plugin;

public class YourPlugin extends JavaPlugin {
    
    private MailboxesAPI mailboxesAPI;
    
    @Override
    public void onEnable() {
        // Get the Mailboxes plugin instance
        Plugin plugin = getServer().getPluginManager().getPlugin("Mailboxes");
        
        if (plugin != null && plugin instanceof Mailboxes) {
            // Get the API instance
            mailboxesAPI = ((Mailboxes) plugin).getAPI();
            getLogger().info("Successfully hooked into Mailboxes API v" + mailboxesAPI.getAPIVersion());
        } else {
            getLogger().warning("Mailboxes plugin not found!");
            return;
        }
    }
    
    public MailboxesAPI getMailboxesAPI() {
        return mailboxesAPI;
    }
}
```

### With Soft Dependencies

If you're using a soft dependency, check if Mailboxes is available:

```java
@Override
public void onEnable() {
    if (getServer().getPluginManager().isPluginEnabled("Mailboxes")) {
        Plugin plugin = getServer().getPluginManager().getPlugin("Mailboxes");
        mailboxesAPI = ((Mailboxes) plugin).getAPI();
        getLogger().info("Mailboxes integration enabled!");
    } else {
        getLogger().info("Mailboxes not found - mail features disabled");
    }
}
```

## API Reference

### MailboxesAPI Class

The main API class providing methods to interact with the Mailboxes plugin.

#### Version Methods

##### `getAPIVersion()`
Returns the current API version string.

**Returns:** `String` - The API version (e.g., "v0.0.3")

**Example:**
```java
String apiVersion = mailboxesAPI.getAPIVersion();
System.out.println("Using Mailboxes API: " + apiVersion);
```

##### `getVersion()`
Returns the Mailboxes plugin version.

**Returns:** `String` - The plugin version (e.g., "v1.2.0")

**Example:**
```java
String pluginVersion = mailboxesAPI.getVersion();
```

#### Message Sending Methods

##### `sendPluginMessageToPlayer(String pluginName, Player player, String content)`
Sends a message from your plugin to a player.

**Parameters:**
- `pluginName` (String) - The name of your plugin (used as the sender)
- `player` (Player) - The player to send the message to
- `content` (String) - The message content

**Returns:** `boolean` - `true` if the message was sent successfully, `false` otherwise

**Example:**
```java
Player player = Bukkit.getPlayer("Steve");
if (player != null) {
    boolean success = mailboxesAPI.sendPluginMessageToPlayer(
        "MyPlugin", 
        player, 
        "Congratulations! You've earned a reward!"
    );
    
    if (success) {
        getLogger().info("Message sent successfully!");
    }
}
```

##### `sendPluginMessageToPlayer(String pluginName, UUID playerUUID, String content)`
Sends a message from your plugin to a player using their UUID. This is useful when the player is offline.

**Parameters:**
- `pluginName` (String) - The name of your plugin (used as the sender)
- `playerUUID` (UUID) - The UUID of the player to send the message to
- `content` (String) - The message content

**Returns:** `boolean` - `true` if the message was sent successfully, `false` otherwise

**Example:**
```java
UUID playerUUID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
boolean success = mailboxesAPI.sendPluginMessageToPlayer(
    "MyPlugin", 
    playerUUID, 
    "This message will be waiting when you log in!"
);
```

#### Mailbox Access Methods

##### `getMailbox(Player player)`
Retrieves a player's mailbox.

**Parameters:**
- `player` (Player) - The player whose mailbox to retrieve

**Returns:** `M_Mailbox` - The player's mailbox wrapper object

**Example:**
```java
Player player = Bukkit.getPlayer("Steve");
if (player != null) {
    M_Mailbox mailbox = mailboxesAPI.getMailbox(player);
    ArrayList<Message> activeMessages = mailbox.getActiveMessages();
    
    player.sendMessage("You have " + activeMessages.size() + " active messages.");
}
```

##### `getMessage(int ID)`
Retrieves a specific message by its ID.

**Parameters:**
- `ID` (int) - The unique ID of the message

**Returns:** `M_Message` - The message wrapper object, or `null` if not found

**Example:**
```java
M_Message message = mailboxesAPI.getMessage(123);
if (message != null) {
    String content = message.getContent();
    String sender = message.getSender();
}
```

### M_Mailbox Class

A wrapper class representing a player's mailbox.

#### Methods

##### `getID()`
**Returns:** `int` - The unique mailbox ID

##### `getOwnerUUID()`
**Returns:** `UUID` - The UUID of the mailbox owner

##### `getActiveMessages()`
**Returns:** `ArrayList<Message>` - List of unarchived messages

##### `getActiveMessage(int ID)`
**Returns:** `Message` - A specific active message by ID

##### `getArchivedMessages()`
**Returns:** `ArrayList<Message>` - List of archived messages

##### `getArchivedMessage(int ID)`
**Returns:** `Message` - A specific archived message by ID

##### `sendListOfActiveMessagesToPlayer(Player player)`
Sends a formatted list of active messages to the player.

**Parameters:**
- `player` (Player) - The player to send the list to

##### `sendListOfArchivedMessagesToPlayer(Player player)`
Sends a formatted list of archived messages to the player.

**Parameters:**
- `player` (Player) - The player to send the list to

### M_Message Class

A wrapper class representing a message.

#### Methods

##### `getID()`
**Returns:** `int` - The unique message ID

##### `getSender()`
**Returns:** `String` - The name/identifier of the message sender

##### `getRecipient()`
**Returns:** `String` - The name of the message recipient

##### `getContent()`
**Returns:** `String` - The message content/text

##### `getDate()`
**Returns:** `Date` - The date when the message was created

##### `getMailboxID()`
**Returns:** `int` - The ID of the mailbox this message belongs to

##### `isArchived()`
**Returns:** `boolean` - Whether the message is archived

##### `hasAttachments()`
**Returns:** `boolean` - Whether the message has item attachments

##### `getAttachments()`
**Returns:** `List<ItemStack>` - List of attached items (empty list if none)

##### `sendContentToPlayer(Player player)`
Sends a formatted display of the message content to the player.

**Parameters:**
- `player` (Player) - The player to send the message to

## Examples

### Example 1: Simple Message Notification

Send a notification to a player when they complete a quest:

```java
public class QuestPlugin extends JavaPlugin {
    
    private MailboxesAPI mailboxesAPI;
    
    @Override
    public void onEnable() {
        setupMailboxesAPI();
    }
    
    private void setupMailboxesAPI() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Mailboxes");
        if (plugin instanceof Mailboxes) {
            mailboxesAPI = ((Mailboxes) plugin).getAPI();
        }
    }
    
    public void onQuestComplete(Player player, String questName) {
        if (mailboxesAPI != null) {
            String message = String.format(
                "Congratulations! You've completed the '%s' quest! Your reward has been added to your account.",
                questName
            );
            
            mailboxesAPI.sendPluginMessageToPlayer("QuestPlugin", player, message);
        }
    }
}
```

### Example 2: Offline Player Notifications

Send messages to offline players that they'll receive when they log in:

```java
public class EconomyPlugin extends JavaPlugin {
    
    private MailboxesAPI mailboxesAPI;
    
    public void notifyPlayerOfPayment(UUID playerUUID, double amount, String reason) {
        if (mailboxesAPI == null) return;
        
        String message = String.format(
            "You received $%.2f! Reason: %s",
            amount,
            reason
        );
        
        boolean sent = mailboxesAPI.sendPluginMessageToPlayer(
            "EconomyPlugin", 
            playerUUID, 
            message
        );
        
        if (sent) {
            getLogger().info("Payment notification sent to player " + playerUUID);
        }
    }
}
```

### Example 3: Checking Player Messages

Read a player's messages and perform actions based on them:

```java
import dansplugins.mailboxes.externalapi.M_Mailbox;
import dansplugins.mailboxes.objects.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public void checkPlayerMessages(Player player) {
    if (mailboxesAPI == null) return;
    
    M_Mailbox mailbox = mailboxesAPI.getMailbox(player);
    ArrayList<Message> messages = mailbox.getActiveMessages();
    
    int messageCount = messages.size();
    
    if (messageCount > 0) {
        player.sendMessage(ChatColor.YELLOW + "You have " + messageCount + " active messages!");
        player.sendMessage(ChatColor.YELLOW + "Use /m list to view them.");
    }
}
```

### Example 4: Integrating with Events

Notify players of important events:

```java
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

@EventHandler
public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Location deathLocation = player.getLocation();
    
    if (mailboxesAPI != null) {
        String message = String.format(
            "You died at coordinates: X=%.0f, Y=%.0f, Z=%.0f. Your items may still be there!",
            deathLocation.getX(),
            deathLocation.getY(),
            deathLocation.getZ()
        );
        
        // Send immediately
        mailboxesAPI.sendPluginMessageToPlayer("DeathNotifier", player, message);
    }
}
```

### Example 5: Bulk Notifications

Send messages to multiple players:

```java
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public void sendServerAnnouncement(String announcement) {
    if (mailboxesAPI == null) return;
    
    // Send to all online players
    for (Player player : Bukkit.getOnlinePlayers()) {
        mailboxesAPI.sendPluginMessageToPlayer(
            "AdminPlugin", 
            player, 
            "Server Announcement: " + announcement
        );
    }
    
    // For offline players, maintain a list of UUIDs to notify
    // Note: Avoid using Bukkit.getOfflinePlayers() as it loads all player data
    // Instead, keep track of specific players you want to notify
    
    // Create a set of online player UUIDs for efficient lookup
    Set<UUID> onlineUUIDs = Bukkit.getOnlinePlayers().stream()
        .map(Player::getUniqueId)
        .collect(Collectors.toSet());
    
    // getTrackedPlayerUUIDs() is a placeholder - implement this method to return
    // the list of player UUIDs you want to notify (e.g., from a database or config)
    List<UUID> playersToNotify = getTrackedPlayerUUIDs();
    for (UUID playerUUID : playersToNotify) {
        // Check if player is not online using the set
        if (!onlineUUIDs.contains(playerUUID)) {
            mailboxesAPI.sendPluginMessageToPlayer(
                "AdminPlugin", 
                playerUUID, 
                "Server Announcement: " + announcement
            );
        }
    }
}
```

### Example 6: Conditional Messaging

Send different messages based on player permissions or states:

```java
public void sendRewardNotification(Player player, String rewardType, int amount) {
    if (mailboxesAPI == null) return;
    
    String message;
    if (player.hasPermission("vip.rewards")) {
        // VIP players get bonus information
        message = String.format(
            "VIP Reward! You've received %d %s! (2x bonus applied)",
            amount * 2,
            rewardType
        );
    } else {
        message = String.format(
            "Reward! You've received %d %s!",
            amount,
            rewardType
        );
    }
    
    mailboxesAPI.sendPluginMessageToPlayer("RewardPlugin", player, message);
}
```

## Best Practices

### 1. Check API Availability

Always check if the API is available before using it:

```java
if (mailboxesAPI != null) {
    // Use the API
} else {
    // Handle gracefully
    getLogger().warning("Mailboxes API not available");
}
```

### 2. Handle Return Values

Check the return value of `sendPluginMessageToPlayer()`:

```java
boolean success = mailboxesAPI.sendPluginMessageToPlayer(pluginName, player, message);
if (!success) {
    getLogger().warning("Failed to send message to player");
    // Consider alternative notification methods
}
```

### 3. Use Descriptive Plugin Names

Use a clear, identifiable name for your plugin when sending messages:

```java
// Good
mailboxesAPI.sendPluginMessageToPlayer("QuestSystem", player, message);

// Less clear
mailboxesAPI.sendPluginMessageToPlayer("Plugin", player, message);
```

### 4. Keep Messages Concise

Messages should be clear and not too long:

```java
// Good
"Quest completed! Reward: 100 gold"

// Too verbose
"Congratulations on completing the quest! As a reward for your efforts and dedication..."
```

### 5. Prefer UUID for Offline Players

When you need to send messages to players who might be offline, use the UUID variant:

```java
// Better for offline players
mailboxesAPI.sendPluginMessageToPlayer("MyPlugin", playerUUID, message);
```

### 6. Version Compatibility

Check the API version if you need specific features:

```java
String apiVersion = mailboxesAPI.getAPIVersion();
// Simple string comparison works for semantic versions like "v0.0.3"
// For more complex version checking, consider a version parsing library
if (apiVersion.compareTo("v0.0.3") >= 0) {
    // Use features available in v0.0.3 and later
}
```

### 7. Error Handling

Wrap API calls in try-catch blocks for production code:

```java
import java.util.logging.Level;

try {
    mailboxesAPI.sendPluginMessageToPlayer("MyPlugin", player, message);
} catch (Exception e) {
    getLogger().log(Level.WARNING, "Error sending Mailboxes message", e);
}
```

### 8. Don't Spam Players

Avoid sending too many messages in quick succession:

```java
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Consider batching or rate-limiting per player
// Note: This is a simplified example showing the concept
// For production use, implement proper per-player cooldown tracking
private Map<UUID, Long> lastMessageTimes = new HashMap<>();
private static final long MESSAGE_COOLDOWN = 60000; // 1 minute

public void sendNotification(Player player, String message) {
    UUID playerUUID = player.getUniqueId();
    long currentTime = System.currentTimeMillis();
    
    Long lastTime = lastMessageTimes.get(playerUUID);
    if (lastTime != null && currentTime - lastTime < MESSAGE_COOLDOWN) {
        return; // Skip if too soon
    }
    
    mailboxesAPI.sendPluginMessageToPlayer("MyPlugin", player, message);
    lastMessageTimes.put(playerUUID, currentTime);
}
```

## Troubleshooting

### API Returns Null

**Problem:** `getAPI()` returns null or the plugin isn't found.

**Solutions:**
- Ensure Mailboxes is installed on the server
- Check that Mailboxes is listed in `depend` or `softdepend` in your `plugin.yml`
- Verify you're accessing the API after `onEnable()`

### Messages Not Sending

**Problem:** `sendPluginMessageToPlayer()` returns false.

**Solutions:**
- Verify the player/UUID exists
- Check server logs for Mailboxes errors
- Ensure the message content is not empty
- Verify Mailboxes plugin is enabled

### Dependency Not Found at Compile Time

**Problem:** Cannot import Mailboxes classes.

**Solutions:**
- Verify the Mailboxes JAR is in your build path
- Check your Maven/Gradle configuration
- Ensure you're using the correct version
- Try rebuilding your project

## Support

For additional help:
- [Mailboxes GitHub Repository](https://github.com/Dans-Plugins/Mailboxes)
- [Support Discord](https://discord.gg/xXtuAQ2)
- [Report Issues](https://github.com/Dans-Plugins/Mailboxes/issues)

## Version History

- **v0.0.3** - Current API version
  - Support for plugin messages with UUID
  - Access to mailboxes and messages
  - Attachment support in messages

## License

The Mailboxes plugin and API are licensed under GPL-3.0. When integrating with Mailboxes, ensure your plugin complies with the license terms.
