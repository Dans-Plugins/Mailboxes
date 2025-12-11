package dansplugins.mailboxes.services;

import dansplugins.mailboxes.objects.TopicMailbox;
import dansplugins.mailboxes.objects.TopicMessage;
import dansplugins.mailboxes.utils.Logger;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestApiServiceTest {

    @Mock
    private TopicService topicService;

    @Mock
    private Logger logger;

    @Mock
    private ConfigService configService;

    private RestApiService restApiService;

    @BeforeEach
    void setUp() {
        when(configService.getInt("apiPort")).thenReturn(7070);
        when(configService.getString("apiKey")).thenReturn("test-api-key");
        restApiService = new RestApiService(topicService, logger, configService);
    }

    @Test
    void testHealthEndpoint() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/health", req -> {
                req.header("X-API-Key", "test-api-key");
            });
            
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("ok"));
        });
        
        restApiService.stop();
    }

    @Test
    void testAuthenticationRequired() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics");
            assertEquals(401, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testAuthenticationWithInvalidKey() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics", req -> {
                req.header("X-API-Key", "wrong-key");
            });
            assertEquals(401, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testCreateTopic() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test description");
        when(topicService.createTopic("test-topic", "Test description")).thenReturn(topic);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics", 
                "{\"name\":\"test-topic\",\"description\":\"Test description\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(201, response.code());
            assertTrue(response.body().string().contains("test-topic"));
        });
        
        restApiService.stop();
    }

    @Test
    void testCreateTopicWithoutName() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics", 
                "{\"description\":\"Test description\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(400, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testCreateTopicThatAlreadyExists() {
        when(topicService.createTopic("test-topic", "Test description")).thenReturn(null);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics", 
                "{\"name\":\"test-topic\",\"description\":\"Test description\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(409, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testGetAllTopics() {
        List<TopicMailbox> topics = new ArrayList<>();
        topics.add(new TopicMailbox(1, "topic1", "Desc1"));
        topics.add(new TopicMailbox(2, "topic2", "Desc2"));
        when(topicService.getAllTopics()).thenReturn(topics);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics", req -> {
                req.header("X-API-Key", "test-api-key");
            });
            
            assertEquals(200, response.code());
            String body = response.body().string();
            assertTrue(body.contains("topic1"));
            assertTrue(body.contains("topic2"));
        });
        
        restApiService.stop();
    }

    @Test
    void testGetTopic() {
        TopicMailbox topic = new TopicMailbox(1, "test-topic", "Test description");
        when(topicService.getTopic("test-topic")).thenReturn(topic);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics/test-topic", req -> {
                req.header("X-API-Key", "test-api-key");
            });
            
            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("test-topic"));
        });
        
        restApiService.stop();
    }

    @Test
    void testGetNonExistentTopic() {
        when(topicService.getTopic("non-existent")).thenReturn(null);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics/non-existent", req -> {
                req.header("X-API-Key", "test-api-key");
            });
            
            assertEquals(404, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testDeleteTopic() {
        when(topicService.deleteTopic("test-topic")).thenReturn(true);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.delete("/api/topics/test-topic", req -> {
                req.header("X-API-Key", "test-api-key");
            });
            
            assertEquals(200, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testDeleteNonExistentTopic() {
        when(topicService.deleteTopic("non-existent")).thenReturn(false);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.delete("/api/topics/non-existent", req -> {
                req.header("X-API-Key", "test-api-key");
            });
            
            assertEquals(404, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testPublishMessage() {
        TopicMessage message = new TopicMessage(1, "test-topic", "ProducerPlugin", "Test content");
        when(topicService.publishMessage("test-topic", "ProducerPlugin", "Test content")).thenReturn(message);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics/test-topic/publish",
                "{\"producerPlugin\":\"ProducerPlugin\",\"content\":\"Test content\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(201, response.code());
            String body = response.body().string();
            assertTrue(body.contains("ProducerPlugin"));
            assertTrue(body.contains("Test content"));
        });
        
        restApiService.stop();
    }

    @Test
    void testPublishMessageWithoutProducerPlugin() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics/test-topic/publish",
                "{\"content\":\"Test content\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(400, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testPublishMessageWithoutContent() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics/test-topic/publish",
                "{\"producerPlugin\":\"ProducerPlugin\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(400, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testConsumeMessages() {
        List<TopicMessage> messages = new ArrayList<>();
        messages.add(new TopicMessage(1, "test-topic", "Producer1", "Content1"));
        messages.add(new TopicMessage(2, "test-topic", "Producer2", "Content2"));
        when(topicService.consumeMessages("test-topic", "ConsumerPlugin")).thenReturn(messages);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics/test-topic/consume?consumerPlugin=ConsumerPlugin",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                });
            
            assertEquals(200, response.code());
            String body = response.body().string();
            assertTrue(body.contains("Content1"));
            assertTrue(body.contains("Content2"));
        });
        
        restApiService.stop();
    }

    @Test
    void testConsumeMessagesWithoutConsumerPlugin() {
        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.get("/api/topics/test-topic/consume",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                });
            
            assertEquals(400, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testSubscribe() {
        when(topicService.subscribe("test-topic", "TestPlugin")).thenReturn(true);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.post("/api/topics/test-topic/subscribe",
                "{\"pluginName\":\"TestPlugin\"}",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                    req.header("Content-Type", "application/json");
                });
            
            assertEquals(200, response.code());
        });
        
        restApiService.stop();
    }

    @Test
    void testUnsubscribe() {
        when(topicService.unsubscribe("test-topic", "TestPlugin")).thenReturn(true);

        restApiService.start();
        
        JavalinTest.test(restApiService.getApp(), (server, client) -> {
            var response = client.delete("/api/topics/test-topic/subscribe?pluginName=TestPlugin",
                req -> {
                    req.header("X-API-Key", "test-api-key");
                });
            
            assertEquals(200, response.code());
        });
        
        restApiService.stop();
    }
}
