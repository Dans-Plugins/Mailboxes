# Quick Start Guide - Mailboxes API

Get started with the Mailboxes API in 5 minutes! This guide will help you send your first message to a player with minimal setup.

## Step 1: Add Dependency (2 minutes)

### Maven Users

Download the Mailboxes JAR and place it in a `libs` folder in your project, then add to your `pom.xml`:

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

### Gradle Users

Place the Mailboxes JAR in a `libs` folder and add to your `build.gradle`:

```groovy
dependencies {
    compileOnly files('libs/Mailboxes-1.2.0.jar')
}
```

### Update plugin.yml

Add Mailboxes as a dependency:

```yaml
name: YourPlugin
version: 1.0
main: com.example.yourplugin.YourPlugin
depend: [Mailboxes]
```

## Step 2: Get API Instance (1 minute)

Add this to your main plugin class:

```java
import dansplugins.mailboxes.Mailboxes;
import dansplugins.mailboxes.externalapi.MailboxesAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class YourPlugin extends JavaPlugin {
    
    private MailboxesAPI mailboxesAPI;
    
    @Override
    public void onEnable() {
        // Get Mailboxes API
        Plugin plugin = getServer().getPluginManager().getPlugin("Mailboxes");
        if (plugin != null && plugin instanceof Mailboxes) {
            mailboxesAPI = ((Mailboxes) plugin).getAPI();
            getLogger().info("Hooked into Mailboxes!");
        } else {
            getLogger().severe("Mailboxes not found!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    public MailboxesAPI getMailboxesAPI() {
        return mailboxesAPI;
    }
}
```

## Step 3: Send Your First Message (1 minute)

That's it! Now you can send messages anywhere in your plugin:

```java
import org.bukkit.entity.Player;

public class MyCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            // Send a message to the player
            boolean success = yourPlugin.getMailboxesAPI().sendPluginMessageToPlayer(
                "YourPlugin",              // Your plugin name
                player,                    // The player
                "Hello from YourPlugin!"   // Your message
            );
            
            if (success) {
                player.sendMessage("§aMessage sent! Check your mailbox with /m list");
            }
        }
        return true;
    }
}
```

## That's It! 🎉

You're now sending persistent messages to players! The player can view your message using `/m list` and `/m open <id>`.

## What's Next?

### Send to Offline Players

Use UUID to send messages to offline players:

```java
UUID playerUUID = UUID.fromString("uuid-here");
mailboxesAPI.sendPluginMessageToPlayer("YourPlugin", playerUUID, "You got a message while offline!");
```

### Real-World Examples

Here are some common use cases:

**Quest Completion:**
```java
public void onQuestComplete(Player player, String questName) {
    mailboxesAPI.sendPluginMessageToPlayer(
        "QuestPlugin",
        player,
        "Quest '" + questName + "' completed! Reward: 100 gold"
    );
}
```

**Payment Notification:**
```java
public void notifyPayment(UUID playerUUID, double amount) {
    String message = String.format("You received $%.2f!", amount);
    mailboxesAPI.sendPluginMessageToPlayer("EconomyPlugin", playerUUID, message);
}
```

**Server Events:**
```java
@EventHandler
public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Location loc = player.getLocation();
    
    mailboxesAPI.sendPluginMessageToPlayer(
        "DeathTracker",
        player,
        String.format("You died at X:%.0f Y:%.0f Z:%.0f", loc.getX(), loc.getY(), loc.getZ())
    );
}
```

## Full Documentation

For complete API documentation, advanced features, and best practices, see [API.md](API.md).

## Common Issues

**Problem:** Can't import Mailboxes classes
- **Solution:** Make sure the Mailboxes JAR is in your build path and you've rebuilt your project

**Problem:** API returns null
- **Solution:** Ensure Mailboxes is installed and listed in your `plugin.yml` dependencies

**Problem:** Messages not sending
- **Solution:** Check that the method returns `true` and verify the player/UUID is valid

## Support

- [Full API Documentation](API.md)
- [GitHub Issues](https://github.com/Dans-Plugins/Mailboxes/issues)
- [Discord Support](https://discord.gg/xXtuAQ2)
