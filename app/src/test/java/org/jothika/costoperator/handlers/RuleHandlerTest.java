package org.jothika.costoperator.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.jothika.costoperator.CostOptimizationRule;
import org.jothika.costoperator.TestMockUtils;
import org.jothika.costoperator.events.EventType;
import org.jothika.costoperator.metrics.MetricType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@EnableKubernetesMockClient
class RuleHandlerTest {

    TestMockUtils testMockUtils;
    private KubernetesMockServer mockServer;
    private KubernetesClient mockClient;

    @BeforeEach
    void setUp() {
        // Setup default mock behavior
        testMockUtils = new TestMockUtils(mockServer);
    }

    // test reconcile rule success
    @ParameterizedTest
    @EnumSource(MetricType.class)
    void testReconcileRuleSuccess(MetricType metricType)
            throws InterruptedException, JsonProcessingException {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule", namespace, podName, metricType, 80);
        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(namespace, podName, "100m", "128Mi");
        testMockUtils.mockPodUsageMetricsK8sApiEndpoints(namespace, podName, "50m", "64Mi");
        testMockUtils.mockEventsApiToEmptyResponse(namespace);
        // Create a RuleHandler object
        RuleHandler ruleHandler = new RuleHandler(rule, mockClient);

        // Call the reconcile method
        ruleHandler.reconcileRule();

        // Verify the result
        ObjectMapper objectMapper = new ObjectMapper();
        Event event =
                objectMapper.readValue(
                        mockServer.getLastRequest().getBody().readUtf8(), Event.class);

        assertEquals(rule.getMetadata().getName(), event.getInvolvedObject().getName());
        assertEquals(rule.getMetadata().getNamespace(), event.getInvolvedObject().getNamespace());
        assertEquals(rule.getMetadata().getUid(), event.getInvolvedObject().getUid());
        assertEquals(rule.getApiVersion(), event.getInvolvedObject().getApiVersion());
        assertEquals(rule.getKind(), event.getInvolvedObject().getKind());
        assertEquals(EventType.NORMAL.getType(), event.getType());
        assertEquals(
                "Metric(" + metricType.getType().toUpperCase() + ") usage percentage: 50.0",
                event.getMessage());
    }
}
