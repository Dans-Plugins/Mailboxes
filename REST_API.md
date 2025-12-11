# Mailboxes REST API Documentation

## Overview

The Mailboxes plugin provides a REST API that allows plugins to send messages to each other without requiring a direct dependency on the Mailboxes plugin. This is achieved through a topic-based messaging system similar to Kafka.

## Configuration

Add the following configuration options to your `config.yml`:

```yaml
apiEnabled: true      # Enable/disable the REST API
apiPort: 8080         # Port for the REST API
apiKey: changeme      # API key for authentication (change this!)
```

**Important**: Always change the default API key to a secure value before using in production.

## Authentication

All API requests require authentication using an API key. Include the API key in the request header:

```
X-API-Key: your-api-key-here
```

## Endpoints

### Health Check

**GET** `/api/health`

Check if the API is running.

**Response:**
```json
{
  "status": "ok",
  "service": "Mailboxes REST API"
}
```

### Create Topic

**POST** `/api/topics`

Create a new topic for plugin-to-plugin messaging.

**Request Body:**
```json
{
  "name": "my-topic",
  "description": "Description of the topic"
}
```

**Response:** (201 Created)
```json
{
  "id": 1,
  "name": "my-topic",
  "description": "Description of the topic",
  "messageCount": 0,
  "unconsumedMessageCount": 0,
  "subscribers": []
}
```

### List All Topics

**GET** `/api/topics`

Get a list of all topics.

**Response:**
```json
[
  {
    "id": 1,
    "name": "my-topic",
    "description": "Description of the topic",
    "messageCount": 5,
    "unconsumedMessageCount": 2,
    "subscribers": ["PluginA", "PluginB"]
  }
]
```

### Get Topic Details

**GET** `/api/topics/{name}`

Get details of a specific topic.

**Response:**
```json
{
  "id": 1,
  "name": "my-topic",
  "description": "Description of the topic",
  "messageCount": 5,
  "unconsumedMessageCount": 2,
  "subscribers": ["PluginA", "PluginB"]
}
```

### Delete Topic

**DELETE** `/api/topics/{name}`

Delete a topic.

**Response:**
```json
{
  "message": "Topic deleted successfully"
}
```

### Publish Message

**POST** `/api/topics/{name}/publish`

Publish a message to a topic.

**Request Body:**
```json
{
  "producerPlugin": "MyPlugin",
  "content": "Message content here"
}
```

**Response:** (201 Created)
```json
{
  "id": 123,
  "topic": "my-topic",
  "producerPlugin": "MyPlugin",
  "content": "Message content here",
  "timestamp": "2025-12-11T00:00:00.000+0000",
  "consumed": false
}
```

### Consume Messages

**GET** `/api/topics/{name}/consume?consumerPlugin=MyPlugin`

Consume all unconsumed messages from a topic. Messages are marked as consumed after retrieval.

**Query Parameters:**
- `consumerPlugin` (required): Name of the plugin consuming the messages

**Response:**
```json
[
  {
    "id": 123,
    "topic": "my-topic",
    "producerPlugin": "PluginA",
    "content": "Message content",
    "timestamp": "2025-12-11T00:00:00.000+0000",
    "consumed": false
  }
]
```

### Subscribe to Topic

**POST** `/api/topics/{name}/subscribe`

Subscribe a plugin to a topic.

**Request Body:**
```json
{
  "pluginName": "MyPlugin"
}
```

**Response:**
```json
{
  "message": "Subscribed successfully"
}
```

### Unsubscribe from Topic

**DELETE** `/api/topics/{name}/subscribe?pluginName=MyPlugin`

Unsubscribe a plugin from a topic.

**Query Parameters:**
- `pluginName` (required): Name of the plugin to unsubscribe

**Response:**
```json
{
  "message": "Unsubscribed successfully"
}
```

## Example Usage

### Using curl

```bash
# Set your API key
API_KEY="your-api-key-here"

# Create a topic
curl -X POST http://localhost:8080/api/topics \
  -H "X-API-Key: $API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"name": "player-events", "description": "Player event notifications"}'

# Publish a message
curl -X POST http://localhost:8080/api/topics/player-events/publish \
  -H "X-API-Key: $API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"producerPlugin": "MyPlugin", "content": "Player joined the server"}'

# Consume messages
curl -X GET "http://localhost:8080/api/topics/player-events/consume?consumerPlugin=MyPlugin" \
  -H "X-API-Key: $API_KEY"
```

### Using Java

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MailboxesApiClient {
    private final String apiUrl = "http://localhost:8080/api";
    private final String apiKey = "your-api-key-here";
    private final HttpClient client = HttpClient.newHttpClient();

    public void publishMessage(String topic, String pluginName, String content) {
        String json = String.format(
            "{\"producerPlugin\":\"%s\",\"content\":\"%s\"}", 
            pluginName, content
        );
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl + "/topics/" + topic + "/publish"))
            .header("X-API-Key", apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
            
        try {
            HttpResponse<String> response = client.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );
            System.out.println("Response: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Error Responses

All errors follow this format:

```json
{
  "error": "Error message here"
}
```

Common HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request (missing or invalid parameters)
- `401` - Unauthorized (invalid or missing API key)
- `404` - Not Found (topic doesn't exist)
- `409` - Conflict (topic already exists)
- `500` - Internal Server Error

## Best Practices

1. **Security**: Always use a strong, unique API key and protect it like a password.
2. **Topic Naming**: Use descriptive topic names (e.g., "player-events", "economy-updates").
3. **Message Size**: Keep message content reasonable in size for better performance.
4. **Consumption**: Consume messages regularly to prevent memory buildup.
5. **Error Handling**: Always handle API errors gracefully in your plugin.

## Limitations

- Messages are stored in memory and persisted to disk
- No message retention policies (messages are kept until consumed)
- No guaranteed message ordering across multiple producers
- Single server only (no clustering support)
