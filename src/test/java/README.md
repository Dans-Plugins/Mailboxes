# Unit Tests

This directory contains the plugin's JUnit test suite. Test classes mirror the package of the class they cover under `src/test/java/dansplugins/mailboxes/`.

## Test Coverage

| Package | Test classes | What they cover |
|---------|--------------|-----------------|
| `dansplugins.mailboxes` | `CommandInterpreterTest` | `/m` subcommand dispatch, per-subcommand permission checks and label/subcommand case handling |
| `commands` | `ArchiveCommandTest`, `ConfigCommandTest`, `DeleteCommandTest`, `HelpCommandTest`, `ListCommandTest`, `OpenCommandTest`, `SendCommandTest`, `StatsCommandTest` | Argument handling and replies for each subcommand, plus tab completion for `list` and `open` |
| `data` | `PersistentDataTest` | Message lookup and message-ID usage across every mailbox |
| `externalapi` | `MailboxesAPITest`, `M_MailboxTest`, `M_MessageTest` | The `MailboxesAPI` lookups other plugins call (see `API.md`), and the `M_Mailbox`/`M_Message` wrappers forwarding each accessor and mutator to the mailbox or message they wrap |
| `factories` | `MailboxFactoryTest`, `MessageFactoryTest` | Mailbox and message creation, including free-ID selection when random IDs collide or run out |
| `listeners` | `JoinListenerTest` | Assigning a mailbox to a joining player before alerting them to unread messages |
| `objects` | `MailboxTest`, `MessageTest` | Pagination of active, archived and unread message lists; message save/load round trips (including `PlayerMessage` and `PluginMessage` UUIDs), attachment accessors, and the `/m open` message display with its action buttons |
| `services` | `ConfigServiceTest`, `MailboxServiceTest`, `MailServiceTest` | Config option validation and repair, mailbox assignment and alerts, and rejection of unsendable messages |
| `utils` | `ArgumentParserTest`, `LoggerTest`, `PermissionCheckerTest`, `SchedulerTest`, `UUIDCheckerTest` | Argument dropping and quoted-argument parsing, debug logging gated on `debugMode` and error logging that prints regardless of it, the missing-permission reply, the hourly autosave task and its log lines, and name/UUID lookups across online and offline players |

## Running the Tests

To run all tests:
```bash
mvn test
```

To run a specific test class:
```bash
mvn test -Dtest=ListCommandTest
mvn test -Dtest=MailboxTest
```

To run a specific test method:
```bash
mvn test -Dtest=ListCommandTest#testExecuteWithPageNumber
```

## Test Dependencies

The tests use:
- **JUnit 4.13.2**: Testing framework
- **Mockito 3.12.4**: Mocking framework for Bukkit/Spigot objects

## Notes

- Tests use Mockito to mock Bukkit/Spigot API objects (`Player`, `CommandSender`, etc.) and the plugin's own services, so no server is needed
- New tests should follow the `@Mock` field setup used by sibling tests in the same package
- `Bukkit.setServer` can only be called once per JVM, so tests that read the static Bukkit server (`SchedulerTest`, `UUIDCheckerTest`) share the mock in `utils/MockServer` and stub what they need on it
