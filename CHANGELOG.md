# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added

- A `Dev Release` workflow, which republishes a rolling `dev` prerelease of `main` on every non-documentation push. This is what Dan's Plugin Manager's experimental channel installs from: `/dpm get mailboxes --experimental` reads `releases/tags/dev`, so without it there is nothing for that command to download. The prerelease is unreleased, unreviewed code and is marked as such.

## [2.0.0-SNAPSHOT-8-8-2026] – 2026-08-08

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
