package org.jothika.costoperator.handlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.jothika.costoperator.TestMockUtils;
import org.jothika.costoperator.events.EventGenerator;
import org.jothika.costoperator.events.EventType;
import org.jothika.costoperator.mail.EmailService;
import org.jothika.costoperator.metrics.MetricType;
import org.jothika.costoperator.metrics.MetricsService;
import org.jothika.costoperator.reconciler.CostOptimizationRule;
import org.jothika.costoperator.reconciler.CostOptimizationRuleStatus;
import org.jothika.costoperator.reconciler.enums.RuleStatus;
import org.jothika.costoperator.reconciler.enums.ThresholdCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@EnableKubernetesMockClient
class RuleHandlerTest {

    TestMockUtils testMockUtils;
    @Mock EmailService emailService;
    private KubernetesMockServer mockServer;
    private KubernetesClient kubernetesClient;
    private RuleHandler ruleHandler;

    @BeforeEach
    void setUp() {
        // Setup default mock behavior
        MockitoAnnotations.openMocks(this);
        EventGenerator eventGenerator = new EventGenerator(kubernetesClient);
        MetricsService metricsService = new MetricsService(kubernetesClient);
        ruleHandler = new RuleHandler(eventGenerator, metricsService, emailService);
        testMockUtils = new TestMockUtils(mockServer);
    }

    // test reconcile rule success
    @ParameterizedTest
    @EnumSource(MetricType.class)
    void testReconcileRuleSuccessNotifyUser(MetricType metricType)
            throws InterruptedException, JsonProcessingException {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule",
                        namespace,
                        podName,
                        metricType,
                        ThresholdCondition.LESSTHAN,
                        80);
        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(namespace, podName, "100m", "128Mi");
        testMockUtils.mockPodUsageMetricsK8sApiEndpoints(namespace, podName, "50m", "64Mi");
        testMockUtils.mockEventsApiToEmptyResponse(namespace);

        // Call the reconcile method
        ruleHandler.reconcileRule(rule);

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
                "Metric("
                        + metricType.getType().toUpperCase()
                        + ") usage percentage: 50.0 is less than threshold: 80.0. Notifying the user.",
                event.getMessage());
    }

    @ParameterizedTest
    @EnumSource(MetricType.class)
    void testReconcileRuleSuccessNoAction(MetricType metricType)
            throws InterruptedException, JsonProcessingException {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule",
                        namespace,
                        podName,
                        metricType,
                        ThresholdCondition.LESSTHAN,
                        20);
        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(namespace, podName, "100m", "128Mi");
        testMockUtils.mockPodUsageMetricsK8sApiEndpoints(namespace, podName, "50m", "64Mi");
        testMockUtils.mockEventsApiToEmptyResponse(namespace);

        // Call the reconcile method
        ruleHandler.reconcileRule(rule);

        // Verify the result
        ObjectMapper objectMapper = new ObjectMapper();
        Event event =
                objectMapper.readValue(
                        mockServer.getLastRequest().getBody().readUtf8(), Event.class);

        assertEquals(
                "Metric("
                        + metricType.getType().toUpperCase()
                        + ") usage percentage: 50.0 is greater than threshold: 20.0. No action needed.",
                event.getMessage());
        assertEquals(rule.getMetadata().getName(), event.getInvolvedObject().getName());
        assertEquals(rule.getMetadata().getNamespace(), event.getInvolvedObject().getNamespace());
        assertEquals(rule.getMetadata().getUid(), event.getInvolvedObject().getUid());
        assertEquals(rule.getApiVersion(), event.getInvolvedObject().getApiVersion());
        assertEquals(rule.getKind(), event.getInvolvedObject().getKind());
        assertEquals(EventType.NORMAL.getType(), event.getType());
    }

    @ParameterizedTest
    @EnumSource(MetricType.class)
    void testReconcileRuleCompletedState(MetricType metricType) {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule",
                        namespace,
                        podName,
                        metricType,
                        ThresholdCondition.LESSTHAN,
                        20);

        CostOptimizationRuleStatus status = new CostOptimizationRuleStatus();
        status.setRuleStatus(RuleStatus.COMPLETED);
        rule.setStatus(status);

        // Call the reconcile method
        assertDoesNotThrow(() -> ruleHandler.reconcileRule(rule));
    }

    @ParameterizedTest
    @EnumSource(ThresholdCondition.class)
    void testIsThresholdCrossedLessThan(ThresholdCondition thresholdCondition) {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule", namespace, podName, MetricType.CPU, thresholdCondition, 80);

        // Call the isThresholdCrossed method
        double metricUsagePercentage = 50.0;
        boolean result = ruleHandler.isThresholdCrossed(rule, metricUsagePercentage);
        String message = ruleHandler.getThresholdEventMessage(rule, metricUsagePercentage);

        // Verify the result based on the threshold condition
        switch (thresholdCondition) {
            case GREATERTHAN:
                assertFalse(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 50.0 is less than threshold: 80.0. No action needed.",
                        message);
                break;
            case EQUALS:
                assertFalse(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 50.0 is not equal to threshold: 80.0. No action needed.",
                        message);
                break;
            case LESSTHAN:
                assertTrue(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 50.0 is less than threshold: 80.0. Notifying the user.",
                        message);
                break;
        }
    }

    @ParameterizedTest
    @EnumSource(ThresholdCondition.class)
    void testIsThresholdCrossedGreaterThan(ThresholdCondition thresholdCondition) {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule", namespace, podName, MetricType.CPU, thresholdCondition, 80);

        // Call the isThresholdCrossed method
        double metricUsagePercentage = 90.0;
        boolean result = ruleHandler.isThresholdCrossed(rule, metricUsagePercentage);
        String message = ruleHandler.getThresholdEventMessage(rule, metricUsagePercentage);

        // Verify the result based on the threshold condition
        switch (thresholdCondition) {
            case GREATERTHAN:
                assertTrue(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 90.0 is greater than threshold: 80.0. Notifying the user.",
                        message);
                break;
            case EQUALS:
                assertFalse(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 90.0 is not equal to threshold: 80.0. No action needed.",
                        message);
                break;
            case LESSTHAN:
                assertFalse(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 90.0 is greater than threshold: 80.0. No action needed.",
                        message);
                break;
        }
    }

    @ParameterizedTest
    @EnumSource(ThresholdCondition.class)
    void testIsThresholdCrossedEquals(ThresholdCondition thresholdCondition) {
        String namespace = "test-namespace";
        String podName = "test-pod";
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        "test-rule", namespace, podName, MetricType.CPU, thresholdCondition, 80);

        // Call the isThresholdCrossed method
        double metricUsagePercentage = 80.0;
        boolean result = ruleHandler.isThresholdCrossed(rule, metricUsagePercentage);
        String message = ruleHandler.getThresholdEventMessage(rule, metricUsagePercentage);

        // Verify the result based on the threshold condition
        switch (thresholdCondition) {
            case GREATERTHAN:
                assertFalse(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 80.0 is less than threshold: 80.0. No action needed.",
                        message);
                break;
            case EQUALS:
                assertTrue(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 80.0 is equal to threshold: 80.0. Notifying the user.",
                        message);
                break;
            case LESSTHAN:
                assertFalse(result);
                assertEquals(
                        "Metric(CPU) usage percentage: 80.0 is greater than threshold: 80.0. No action needed.",
                        message);
                break;
        }
    }
}
