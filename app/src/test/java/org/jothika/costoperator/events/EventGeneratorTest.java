package org.jothika.costoperator.events;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import java.net.HttpURLConnection;
import org.jothika.costoperator.CostOptimizationRule;
import org.jothika.costoperator.TestMockUtils;
import org.jothika.costoperator.metrics.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient
class EventGeneratorTest {

    KubernetesMockServer mockServer;
    private KubernetesClient client;
    TestMockUtils testMockUtils;

    @BeforeEach
    void setUp() {
        // Setup default mock behavior
        testMockUtils = new TestMockUtils(mockServer);
    }

    @Test
    void generateEvent() throws InterruptedException, JsonProcessingException {
        // Given
        String namespace = "test-namespace";
        String podName = "test-pod";
        String message = "Test event message";
        EventType eventType = EventType.NORMAL;

        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "sample-rule", namespace, podName, MetricType.CPU, 30);
        testMockUtils.mockEventsApiToEmptyResponse(namespace);

        // When
        EventGenerator eventGenerator = new EventGenerator(client);
        eventGenerator.generateEvent(rule, podName, message, eventType);

        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        Event event =
                objectMapper.readValue(
                        mockServer.getLastRequest().getBody().readUtf8(), Event.class);

        assertEquals(rule.getMetadata().getName(), event.getInvolvedObject().getName());
        assertEquals(rule.getMetadata().getNamespace(), event.getInvolvedObject().getNamespace());
        assertEquals(rule.getMetadata().getUid(), event.getInvolvedObject().getUid());
        assertEquals(rule.getApiVersion(), event.getInvolvedObject().getApiVersion());
        assertEquals(rule.getKind(), event.getInvolvedObject().getKind());
        assertEquals(eventType.getType(), event.getType());
        assertEquals(message, event.getMessage());
    }

    @Test
    void generateEventFailure() {
        // Given
        String namespace = "test-namespace";
        String podName = "test-pod";
        String message = "Test event message";
        EventType eventType = EventType.NORMAL;

        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "sample-rule", namespace, podName, MetricType.CPU, 30);

        mockServer
                .expect()
                .post()
                .withPath(String.format("/api/v1/namespaces/%s/events", namespace))
                .andReturn(HttpURLConnection.HTTP_NOT_FOUND, "{}")
                .once();

        // When
        EventGenerator eventGenerator = new EventGenerator(client);

        // Then
        Exception exception =
                assertThrows(
                        KubernetesClientException.class,
                        () -> {
                            eventGenerator.generateEvent(rule, podName, message, eventType);
                        });
        String expectedMessage = "Failure executing: POST";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
