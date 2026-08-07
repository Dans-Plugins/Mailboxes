# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- `/m stats` command to view total, active, archived, and unread message counts

### Fixed
- `/m delete` and `/m archive` no longer crash on a non-numeric message ID or when the sender has no mailbox; they now show a friendly error message instead
- Documentation now matches the implementation: the `assignmentAlertEnabled`, `welcomeMessageEnabled`, and `quotesEnabled` descriptions in `CONFIG.md`, the message-ID arguments on `/m open`, `/m delete`, and `/m archive` in `COMMANDS.md` and `USER_GUIDE.md`, the return semantics of `MailboxesAPI.getMailbox` and `MailboxesAPI.getMessage` plus the previously undocumented `M_Mailbox` mutators in `API.md`, the attachment limits described in `ATTACHMENTS.md`, and the plugin version used in the dependency examples in `API.md` and `QUICKSTART.md`

## [1.3.0]

### Added
- Player-to-player messaging with persistent storage
- Item attachment support (`-attach` flag)
- Message listing with type filter and pagination
- Archive and delete operations
- In-game config management via `/m config`
