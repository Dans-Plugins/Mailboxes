package dansplugins.mailboxes.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.mailboxes.objects.TopicMailbox;
import dansplugins.mailboxes.objects.TopicMessage;
import dansplugins.mailboxes.utils.Logger;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestApiService {
    private final TopicService topicService;
    private final Logger logger;
    private final ConfigService configService;
    private Javalin app;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public RestApiService(TopicService topicService, Logger logger, ConfigService configService) {
        this.topicService = topicService;
        this.logger = logger;
        this.configService = configService;
    }

    public void start() {
        int port = configService.getInt("apiPort");
        logger.log("Starting REST API on port " + port);

        app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(port);

        setupRoutes();
        logger.log("REST API started successfully");
    }

    public void stop() {
        if (app != null) {
            logger.log("Stopping REST API");
            app.stop();
            logger.log("REST API stopped");
        }
    }

    private void setupRoutes() {
        // Create a new topic
        app.post("/api/topics", this::createTopic);

        // Get all topics
        app.get("/api/topics", this::getAllTopics);

        // Get a specific topic
        app.get("/api/topics/:name", this::getTopic);

        // Delete a topic
        app.delete("/api/topics/:name", this::deleteTopic);

        // Publish a message to a topic
        app.post("/api/topics/:name/publish", this::publishMessage);

        // Consume messages from a topic
        app.get("/api/topics/:name/consume", this::consumeMessages);

        // Subscribe to a topic
        app.post("/api/topics/:name/subscribe", this::subscribe);

        // Unsubscribe from a topic
        app.delete("/api/topics/:name/subscribe", this::unsubscribe);

        // Health check
        app.get("/api/health", ctx -> {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("service", "Mailboxes REST API");
            ctx.json(response);
        });
    }

    private void createTopic(Context ctx) {
        try {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String name = body.get("name");
            String description = body.get("description");

            if (name == null || name.trim().isEmpty()) {
                ctx.status(400).json(createErrorResponse("Topic name is required"));
                return;
            }

            TopicMailbox topic = topicService.createTopic(name, description != null ? description : "");
            
            if (topic == null) {
                ctx.status(409).json(createErrorResponse("Topic already exists"));
                return;
            }

            ctx.status(201).json(topicToMap(topic));
        } catch (Exception e) {
            logger.log("Error creating topic: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void getAllTopics(Context ctx) {
        try {
            List<TopicMailbox> topics = topicService.getAllTopics();
            List<Map<String, Object>> topicMaps = topics.stream()
                .map(this::topicToMap)
                .collect(Collectors.toList());
            ctx.json(topicMaps);
        } catch (Exception e) {
            logger.log("Error getting topics: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void getTopic(Context ctx) {
        try {
            String name = ctx.pathParam("name");
            TopicMailbox topic = topicService.getTopic(name);

            if (topic == null) {
                ctx.status(404).json(createErrorResponse("Topic not found"));
                return;
            }

            ctx.json(topicToMap(topic));
        } catch (Exception e) {
            logger.log("Error getting topic: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void deleteTopic(Context ctx) {
        try {
            String name = ctx.pathParam("name");
            boolean deleted = topicService.deleteTopic(name);

            if (!deleted) {
                ctx.status(404).json(createErrorResponse("Topic not found"));
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Topic deleted successfully");
            ctx.json(response);
        } catch (Exception e) {
            logger.log("Error deleting topic: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void publishMessage(Context ctx) {
        try {
            String topicName = ctx.pathParam("name");
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String producerPlugin = body.get("producerPlugin");
            String content = body.get("content");

            if (producerPlugin == null || producerPlugin.trim().isEmpty()) {
                ctx.status(400).json(createErrorResponse("Producer plugin name is required"));
                return;
            }

            if (content == null || content.trim().isEmpty()) {
                ctx.status(400).json(createErrorResponse("Message content is required"));
                return;
            }

            TopicMessage message = topicService.publishMessage(topicName, producerPlugin, content);

            if (message == null) {
                ctx.status(404).json(createErrorResponse("Topic not found"));
                return;
            }

            ctx.status(201).json(messageToMap(message));
        } catch (Exception e) {
            logger.log("Error publishing message: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void consumeMessages(Context ctx) {
        try {
            String topicName = ctx.pathParam("name");
            String consumerPlugin = ctx.queryParam("consumerPlugin");

            if (consumerPlugin == null || consumerPlugin.trim().isEmpty()) {
                ctx.status(400).json(createErrorResponse("Consumer plugin name is required"));
                return;
            }

            List<TopicMessage> messages = topicService.consumeMessages(topicName, consumerPlugin);

            if (messages == null) {
                ctx.status(404).json(createErrorResponse("Topic not found"));
                return;
            }

            List<Map<String, Object>> messageMaps = messages.stream()
                .map(this::messageToMap)
                .collect(Collectors.toList());
            ctx.json(messageMaps);
        } catch (Exception e) {
            logger.log("Error consuming messages: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void subscribe(Context ctx) {
        try {
            String topicName = ctx.pathParam("name");
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String pluginName = body.get("pluginName");

            if (pluginName == null || pluginName.trim().isEmpty()) {
                ctx.status(400).json(createErrorResponse("Plugin name is required"));
                return;
            }

            boolean subscribed = topicService.subscribe(topicName, pluginName);

            if (!subscribed) {
                ctx.status(404).json(createErrorResponse("Topic not found"));
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Subscribed successfully");
            ctx.json(response);
        } catch (Exception e) {
            logger.log("Error subscribing: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private void unsubscribe(Context ctx) {
        try {
            String topicName = ctx.pathParam("name");
            String pluginName = ctx.queryParam("pluginName");

            if (pluginName == null || pluginName.trim().isEmpty()) {
                ctx.status(400).json(createErrorResponse("Plugin name is required"));
                return;
            }

            boolean unsubscribed = topicService.unsubscribe(topicName, pluginName);

            if (!unsubscribed) {
                ctx.status(404).json(createErrorResponse("Topic not found"));
                return;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Unsubscribed successfully");
            ctx.json(response);
        } catch (Exception e) {
            logger.log("Error unsubscribing: " + e.getMessage());
            ctx.status(500).json(createErrorResponse("Internal server error"));
        }
    }

    private Map<String, Object> topicToMap(TopicMailbox topic) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", topic.getID());
        map.put("name", topic.getName());
        map.put("description", topic.getDescription());
        map.put("messageCount", topic.getMessages().size());
        map.put("unconsumedMessageCount", topic.getUnconsumedMessages().size());
        map.put("subscribers", topic.getSubscribedPlugins());
        return map;
    }

    private Map<String, Object> messageToMap(TopicMessage message) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", message.getID());
        map.put("topic", message.getTopic());
        map.put("producerPlugin", message.getProducerPlugin());
        map.put("content", message.getContent());
        map.put("timestamp", message.getTimestamp());
        map.put("consumed", message.isConsumed());
        return map;
    }

    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}
