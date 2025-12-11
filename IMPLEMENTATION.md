# Plugin-to-Plugin Messaging Implementation Summary

## Overview

This implementation adds a topic-based messaging system with a REST API that allows plugins to communicate with each other without requiring a direct dependency on the Mailboxes plugin. This addresses the issue requirement to "allow plugins to send messages to each other" through a Kafka-like simplified implementation.

## What Was Implemented

### 1. Topic-Based Data Structures

**TopicMessage.java**
- Represents a message published to a topic
- Contains: ID, topic name, producer plugin name, content, timestamp, consumed flag
- Implements the Savable interface for persistence

**TopicMailbox.java**
- Represents a topic (similar to a Kafka topic)
- Contains: ID, name, description, list of messages, list of subscribed plugins
- Provides methods for adding/removing messages and managing subscribers
- Tracks unconsumed messages for efficient consumption

### 2. Service Layer

**TopicService.java**
- Core business logic for topic operations
- Handles topic creation, deletion, and retrieval
- Manages message publishing and consumption
- Handles plugin subscriptions to topics
- Automatically initializes ID counters based on existing data to prevent collisions

**RestApiService.java**
- HTTP server implementation using Javalin web framework
- Exposes REST API endpoints for topic operations
- Implements API key-based authentication with constant-time comparison
- Provides comprehensive error handling and validation

### 3. Persistence Layer

Updated **StorageService.java** to:
- Save and load TopicMailboxes to/from JSON
- Save and load TopicMessages to/from JSON
- Persist subscriber lists for each topic
- Store data in separate files: `topicMailboxes.json` and `topicMessages.json`

Updated **PersistentData.java** to:
- Manage collections of TopicMailboxes and TopicMessages
- Provide lookup methods for topics and messages
- Add/remove operations for both topics and messages

### 4. Configuration

Enhanced **ConfigService.java** with:
- `apiEnabled` (boolean) - Enable/disable the REST API (default: true)
- `apiPort` (integer) - Port for the REST API (default: 8080)
- `apiKey` (string) - API key for authentication (auto-generated secure random 32-char key)
- Secure API key generation using SecureRandom
- API key is hidden in config list output for security

### 5. REST API Endpoints

All endpoints require authentication via `X-API-Key` header.

#### Topic Management
- `POST /api/topics` - Create a new topic
- `GET /api/topics` - List all topics
- `GET /api/topics/{name}` - Get topic details
- `DELETE /api/topics/{name}` - Delete a topic

#### Message Operations
- `POST /api/topics/{name}/publish` - Publish a message to a topic
- `GET /api/topics/{name}/consume` - Consume unconsumed messages from a topic

#### Subscription Management
- `POST /api/topics/{name}/subscribe` - Subscribe a plugin to a topic
- `DELETE /api/topics/{name}/subscribe` - Unsubscribe a plugin from a topic

#### Health Check
- `GET /api/health` - Check API status

### 6. Security Features

1. **API Key Authentication**
   - All API endpoints require a valid API key
   - API key is auto-generated on first run (32-character random string)
   - Constant-time comparison prevents timing attacks

2. **Input Validation**
   - All endpoints validate required parameters
   - Proper HTTP status codes for different error conditions

3. **Dependency Security**
   - Updated to Javalin 5.6.5 (latest secure version)
   - No known vulnerabilities in dependencies

### 7. Documentation

**REST_API.md**
- Complete API documentation with examples
- Authentication instructions
- Endpoint descriptions with request/response formats
- Example usage with curl and Java
- Best practices and limitations

Updated **README.md**
- Added features section highlighting new capabilities
- Added plugin developers section
- Quick example of API usage
- Link to full REST API documentation

## How It Works

### Message Flow (Producer-Consumer Pattern)

1. **Topic Creation**: A plugin creates a topic via REST API
   ```
   POST /api/topics
   {"name": "player-events", "description": "Player event notifications"}
   ```

2. **Subscription** (Optional): Consumer plugins subscribe to topics
   ```
   POST /api/topics/player-events/subscribe
   {"pluginName": "ConsumerPlugin"}
   ```

3. **Publishing**: Producer plugin publishes messages
   ```
   POST /api/topics/player-events/publish
   {"producerPlugin": "ProducerPlugin", "content": "Player joined"}
   ```

4. **Consumption**: Consumer plugin retrieves messages
   ```
   GET /api/topics/player-events/consume?consumerPlugin=ConsumerPlugin
   ```

5. **Persistence**: All topics and messages are automatically saved to disk

### Key Design Decisions

1. **No Dependency Required**: Plugins communicate via HTTP REST API, eliminating the need to add Mailboxes as a dependency

2. **Kafka-Like Simplicity**: Topic-based messaging with publish/subscribe pattern, but simplified for Minecraft plugin use case

3. **Message Consumption**: Messages are marked as consumed after retrieval, preventing duplicate processing

4. **In-Memory with Persistence**: Data is kept in memory for performance but persisted to disk for durability

5. **Secure by Default**: Auto-generated API key ensures security without requiring manual configuration

## Configuration Example

After first run, `config.yml` will contain:
```yaml
apiEnabled: true
apiPort: 8080
apiKey: "randomly-generated-32-char-key-here"
```

## Benefits

1. **Decoupled Architecture**: Plugins can communicate without knowing about each other
2. **No Dependencies**: No need to add Mailboxes as a dependency in your plugin
3. **Simple Integration**: Standard HTTP REST API, works with any HTTP client
4. **Persistent Messaging**: Messages survive server restarts
5. **Topic Organization**: Logical grouping of related messages
6. **Secure**: API key authentication protects against unauthorized access

## Limitations

1. **Single Server Only**: No clustering or multi-server support
2. **In-Memory Storage**: Large volumes of messages may consume memory
3. **No Message TTL**: Messages persist until consumed (no automatic expiration)
4. **Simple Ordering**: Messages are ordered within a single topic but not globally
5. **No Partitioning**: Unlike Kafka, no partition support for parallel processing

## Testing

A test script (`/tmp/test_api.sh`) has been created to validate all API endpoints. The script tests:
- Health check
- Topic creation, listing, and deletion
- Message publishing and consumption
- Subscription management
- Authentication

## Future Enhancements

Potential improvements for future releases:
1. Message TTL (time-to-live) and automatic cleanup
2. Message filtering/querying capabilities
3. Websocket support for real-time notifications
4. Admin UI for topic management
5. Message replay capabilities
6. Rate limiting per plugin
7. Multi-server support (if Kafka integration is needed)

## Security Considerations

1. **Change API Key**: Always use the auto-generated key and keep it secure
2. **Network Security**: Bind to localhost only if plugins run on same server
3. **Monitor Usage**: Track which plugins are using the API
4. **Rate Limiting**: Consider implementing rate limits for production use
5. **HTTPS**: For production, consider adding HTTPS support

## Conclusion

This implementation successfully fulfills the requirement to allow plugins to send messages to each other through a REST API. The topic-based approach provides a flexible, scalable solution that doesn't require plugins to depend on Mailboxes directly. The implementation is secure, well-documented, and ready for use by plugin developers.
