# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

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
