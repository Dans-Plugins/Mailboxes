# Mailboxes

## Description
Mailboxes is an open source plugin that allows players and plugins to send persistent messages to players. Players can also attach items to their messages, making it easy to transfer items securely through in-game mail.

## Features
- **Persistent Messaging**: Send messages that persist across server restarts
- **Item Attachments**: Attach items to messages for secure item transfer
- **Configurable Limits**: Server admins can configure attachment limits
- **Permission-Based**: Control who can use attachment features
- **API Support**: External plugins can integrate with Mailboxes

For more information about the item attachments feature, see [ATTACHMENTS.md](ATTACHMENTS.md).

## For Plugin Developers
Want to integrate Mailboxes into your plugin? Get started in minutes!

- **[Quick Start Guide](QUICKSTART.md)** - Send your first message in 5 minutes
- **[Complete API Documentation](API.md)** - Full API reference with examples

### Key Features for Developers
- Simple API - send messages with just a few lines of code
- Support for online and offline players
- Messages persist across server restarts
- No complex setup required

## Server Software
This plugin was developed using the Spigot API. Users may run into trouble using it with other available server softwares like Paper.

## Installation
1) You can download the plugin from [this page](https://www.spigotmc.org/resources/mailboxes.96611/).

2) Once downloaded, place the jar in the plugins folder of your server files.

3) Restart your server.

## Usage
- [User Guide](https://github.com/dmccoystephenson/Mailboxes/wiki/Guide) (coming soon)
- [List of Commands](https://github.com/dmccoystephenson/Mailboxes/wiki/Commands)
- [FAQ](https://github.com/dmccoystephenson/Mailboxes/wiki/FAQ) (coming soon)

## Support
You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?
Please fill out a bug report [here](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aissue+is%3Aopen+label%3Abug).

## Roadmap
- [Known Bugs](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aopen+is%3Aissue+label%3Abug)
- [Planned Features](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/dmccoystephenson/Mailboxes/issues?q=is%3Aopen+is%3Aissue+label%3Aimprovement)

## Contributing
- [API Documentation for Plugin Developers](API.md)
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

## Usage reporting

Usage reporting is on by default: when the plugin is enabled, and each time one of its commands is used, it sends its name, version and the command name to the author's [trace](https://trace.danielstephenson.dev) server so it is known which plugins are actually in use. Nothing about players, worlds, IPs or the server is sent, and nothing typed after a command. The plugin says on the console at every start whether reporting is on. To turn it off:

- for this plugin: set `usage-reporting.enabled` to `false` in `plugins/Mailboxes/config.yml`;
- for every plugin on the server that reports to trace: set `enabled` to `false` in `plugins/trace/config.yml` (created on first start);
- for the whole server process: set the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1`.

Details: https://github.com/Stephenson-Software/trace#usage-reporting
