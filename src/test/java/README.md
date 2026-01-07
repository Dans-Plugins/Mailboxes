# Unit Tests for Pagination Feature

This directory contains unit tests for the pagination system implemented for the `/m list` command.

## Test Coverage

### ListCommandTest
Tests the command parsing and execution logic for the ListCommand class:

- **testExecuteWithNonPlayerSender**: Verifies that non-player senders cannot use the command
- **testExecuteWithNoMailbox**: Verifies proper error handling when player has no mailbox
- **testExecuteWithNoArgsDefaultsToActivePage1**: Verifies default behavior (active messages, page 1)
- **testExecuteWithActiveListType**: Verifies "active" list type navigation
- **testExecuteWithArchivedListType**: Verifies "archived" list type navigation
- **testExecuteWithUnreadListType**: Verifies "unread" list type navigation
- **testExecuteWithPageNumber**: Verifies page number parsing and navigation
- **testExecuteWithInvalidPageNumber**: Verifies error handling for non-numeric page numbers
- **testExecuteWithNegativePageNumber**: Verifies error handling for page numbers < 1
- **testExecuteWithInvalidListType**: Verifies error handling for invalid list types
- **testGetTabCompletionsForListType**: Verifies tab completion filtering
- **testGetTabCompletionsReturnsAllTypesOnEmpty**: Verifies tab completion with empty input
- **testGetTabCompletionsForOtherArguments**: Verifies tab completion for other arguments

### MailboxPaginationTest
Tests the pagination logic and message display for the Mailbox class:

- **testEmptyActiveMessagesShowsAppropriateMessage**: Verifies message for empty mailbox
- **testEmptyActiveMessagesWithHighPageShowsError**: Verifies error for high page on empty mailbox
- **testSinglePageOfMessages**: Verifies display when all messages fit on one page
- **testMultiplePagesShowsNavigation**: Verifies navigation links appear for multiple pages
- **testPageBoundaryCalculation**: Verifies correct page boundaries for middle pages
- **testLastPageWithPartialMessages**: Verifies correct display for last page with fewer messages
- **testInvalidPageNumberTooHigh**: Verifies error handling for page numbers exceeding total pages
- **testInvalidPageNumberNegative**: Verifies error handling for negative page numbers
- **testArchivedMessagesPagination**: Verifies pagination works for archived messages
- **testUnreadMessagesPagination**: Verifies pagination works for unread messages (cross-list)
- **testPageSizeRespected**: Verifies custom page size is correctly applied
- **testDefaultPageSizeUsedByOverloadedMethod**: Verifies default page size (10) is used by convenience methods

## Running the Tests

To run all tests:
```bash
mvn test
```

To run a specific test class:
```bash
mvn test -Dtest=ListCommandTest
mvn test -Dtest=MailboxPaginationTest
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

- Tests use Mockito to mock Bukkit/Spigot API objects (Player, CommandSender, etc.)
- Tests verify both happy path and error handling scenarios
- Tests ensure backward compatibility with existing command usage
- Tests validate the clickable navigation components are being sent to players
