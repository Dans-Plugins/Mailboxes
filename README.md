# Mailboxes

## Description
Mailboxes is an open source plugin that allows players and plugins to send persistent messages to players.

## Features

- **Player Messaging**: Players can send and receive persistent messages
- **Plugin Integration**: Plugins can send messages to players via external API
- **Plugin-to-Plugin Messaging**: Plugins can communicate with each other via REST API without requiring a dependency
- **Topic-Based System**: Create topics for organized message exchange between plugins
- **Persistent Storage**: All messages and topics are saved to disk
- **Secure REST API**: API key authentication for secure plugin communication

## Server Software
This plugin was developed using the Spigot API. Users may run into trouble using it with other available server softwares like Paper.

## Installation
1) You can download the plugin from [this page](https://www.spigotmc.org/resources/mailboxes.96611/).

2) Once downloaded, place the jar in the plugins folder of your server files.

3) Restart your server.

## Usage
- [User Guide](https://github.com/dmccoystephenson/Mailboxes/wiki/Guide) (coming soon)
- [List of Commands](https://github.com/dmccoystephenson/Mailboxes/wiki/Commands)
- [REST API Documentation](REST_API.md) - For plugin developers
- [FAQ](https://github.com/dmccoystephenson/Mailboxes/wiki/FAQ) (coming soon)

## For Plugin Developers

The Mailboxes plugin provides a REST API that allows your plugin to communicate with other plugins without requiring a direct dependency. This is perfect for:

- Inter-plugin communication
- Event broadcasting
- Data sharing between plugins
- Loosely-coupled plugin architecture

See the [REST API Documentation](REST_API.md) for complete details on how to use the API.

### Quick Example

```bash
# Create a topic
curl -X POST http://localhost:8080/api/topics \
  -H "X-API-Key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{"name": "player-events", "description": "Player events"}'

# Publish a message
curl -X POST http://localhost:8080/api/topics/player-events/publish \
  -H "X-API-Key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{"producerPlugin": "MyPlugin", "content": "Player joined"}'
```

## Support
You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?
Please fill out a bug report [here](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aissue+is%3Aopen+label%3Abug).

## Roadmap
- [Known Bugs](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aopen+is%3Aissue+label%3Abug)
- [Planned Features](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aopen+is%3Aissue+label%3Aimprovement)

## Contributing
- [Notes for Developers](https://github.com/dmccoystephenson/Mailboxes/wiki/Developer-Notes) (coming soon)

## Authors and acknowledgement
Name | Main Contributions
------------ | -------------
Daniel Stephenson | Creator

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE) (GPL-3.0).

You are free to use, modify, and distribute this software, provided that:
- Source code is made available under the same license when distributed.
- Changes are documented and attributed.
- No additional restrictions are applied.

See the [LICENSE](LICENSE) file for the full text of the GPL-3.0 license.

## Project Status
This project is in active development.

### bStats
You can find the bStats page for this plugin [here](https://bstats.org/plugin/bukkit/Mailboxes/12902).
