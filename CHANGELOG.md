# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Fixed

- Unusable values in `config.yml` are now replaced by their defaults when the plugin is enabled, with a console warning naming the option, the value found, and the default used. An integer option holding a value that is not a whole number of at least `1`, or a boolean option holding a value that is not `true` or `false`, was previously read as-is: a hand-edited `maxMessageIDNumber: 0` (or a file written before `/m config set` validated it) made every attempt to create a message throw out of `Random.nextInt`, with nothing on the console pointing at the configuration as the cause
- `/m config set` now rejects a value for a boolean option that is not `true` or `false`. Any other value — `yes`, `1`, a misspelling of `true` — was accepted, stored as `false`, and reported as `Boolean set.`, so an operator meaning to enable an option was told the change succeeded while the opposite was written; for `attachmentsEnabled` that silently disabled item attachments. Such a value is now refused with an explanation and the option is left unchanged

### Changed

- The dependency examples in `API.md` now reference the `1.4.0` release instead of the `2.0.0-SNAPSHOT-8-8-2026` snapshot

## [1.4.0] – 2026-09-19

### Added

- The plugin now reports usage events — `startup` on enable, `command` on each of its commands — to the author's trace server so it is known which plugins are in use. Events carry the plugin name, the event name, and the plugin version or command name; nothing about players or the server. Reporting runs off the main thread, never delays a tick, drops silently when the server is unreachable, and is turned off with `usage-reporting.enabled: false` in `config.yml`. The default config carries the plugin's key, so reporting is active out of the box unless turned off — including on servers upgraded from a version before the `usage-reporting` block existed, whose `config.yml` is not rewritten: the plugin reads the bundled defaults for any key the file lacks
- The plugin says on the console at every start whether usage reporting is on and how to turn it off. A server-wide switch, `plugins/trace/config.yml`, is created on first start and honoured by every plugin that reports to trace; the environment variables `TRACE_USAGE_REPORTING=off` and `DO_NOT_TRACK=1` turn it off for the whole process. A `config.yml` that predates the `usage-reporting` block is now rewritten with it on the next start, not only when the plugin version changes, so the opt-out is visible in the file.
- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get mailboxes --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

### Fixed

- `/m config set` now rejects an unusable value for `maxMessageIDNumber`, `maxMailboxIDNumber`, or `maxAttachmentStackSize` instead of storing it. A value of `0` or below was accepted, after which every attempt to create a message or a mailbox threw out of `Random.nextInt` and message creation was disabled server-wide until `config.yml` was corrected by hand; a non-numeric value threw a `NumberFormatException` at the command sender in place of a usage message. Such a value is now refused with an explanation and the option is left unchanged. Values already present in `config.yml` are still read as-is, so a hand-edited file can still hold an unusable one
- A new message is no longer given an ID that another message already holds when the random draws for a free ID keep landing on taken ones. A bounded number of draws was attempted and the last candidate was then handed out without being re-checked, which on a server whose message count approaches `maxMessageIDNumber` is the expected outcome rather than an unlikely one. The lowest free ID in the configured range is now used once the draws are exhausted. Where the whole range is in use, the message is refused instead of a duplicate ID being issued: an error is logged to the console, `/m send` tells the sender the message could not be created, and `MailboxesAPI.sendPluginMessageToPlayer` returns `false`
- The `Dev Release` workflow now retries publishing the `dev` prerelease before giving up. The release and its tag have to be deleted and recreated for the tag to move to the new commit, and a transient API failure inside that window previously left the repository with no `dev` release at all until the workflow was re-run by hand. Each attempt now starts from a clean slate, and an exhausted retry fails loudly.
- New messages are no longer given an ID that an archived message already holds. The uniqueness check only searched active messages, so an archived message's ID counted as free; once reissued, `/m open`, `/m delete`, and `/m archive` could no longer reach the archived message
- The in-game help menu now shows the message-ID argument on `/m open`, `/m delete`, and `/m archive`, and lists the `active`, `archived`, and `unread` values accepted by `/m list`
- The `/m config set` usage string no longer refers to a `/c` command, which the plugin does not register
- The `/m send` usage string now shows the double quotes the command actually requires around the message, rather than single quotes

## [2.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

A dated snapshot, not a stable release. Everything listed here was on `main` when `1.4.0` was cut and ships in that release; `1.4.0` is the latest stable version despite sorting below this heading numerically.

### Changed
- Mailboxes is now developed AI-first. Day-to-day feature work, grooming, review and maintenance run through AI agents working directly against this repository, with the maintainers setting direction and approving what lands. The major version bump marks that change in how the project is built — it is not a break in behaviour, configuration or stored data, and existing installations can upgrade in place. Released as `2.0.0-SNAPSHOT-8-8-2026`: the AI-first line has not yet been verified in live operation, and the dated snapshot designation stays until it has.

### Added
- `/m stats` command to view total, active, archived, and unread message counts

### Fixed
- `/m delete` and `/m archive` no longer crash on a non-numeric message ID or when the sender has no mailbox; they now show a friendly error message instead
- `CONFIG.md` descriptions of `assignmentAlertEnabled`, `welcomeMessageEnabled`, and `quotesEnabled`, which described behavior the plugin does not have
- Missing message-ID arguments on `/m open`, `/m delete`, and `/m archive` in `COMMANDS.md` and `USER_GUIDE.md`, and the missing `/m config` sub-commands
- `API.md` return semantics for `MailboxesAPI.getMailbox` and `MailboxesAPI.getMessage`, plus the previously undocumented `M_Mailbox` mutators
- An `ATTACHMENTS.md` reference to a max-attachments setting that does not exist, and dependency examples in `API.md` and `QUICKSTART.md` still pinned to 1.2.0

## [1.3.0]

### Added
- Player-to-player messaging with persistent storage
- Item attachment support (`-attach` flag)
- Message listing with type filter and pagination
- Archive and delete operations
- In-game config management via `/m config`
