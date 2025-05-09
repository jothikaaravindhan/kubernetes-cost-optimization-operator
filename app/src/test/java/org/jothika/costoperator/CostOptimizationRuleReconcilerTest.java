package org.jothika.costoperator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jothika.costoperator.events.EventGenerator;
import org.jothika.costoperator.handlers.RuleHandler;
import org.jothika.costoperator.mail.EmailService;
import org.jothika.costoperator.metrics.MetricType;
import org.jothika.costoperator.metrics.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@EnableKubernetesMockClient
class CostOptimizationRuleReconcilerTest {

    @Mock Context<CostOptimizationRule> context;
    @Mock EmailService emailService;
    private KubernetesMockServer mockServer;
    private KubernetesClient mockClient;
    private TestMockUtils testMockUtils;
    private CostOptimizationRuleReconciler reconciler;

    @BeforeEach
    void setUp() {
        // Initialize @Mock fields
        MockitoAnnotations.openMocks(this);
        // Setup default mock behavior
        when(context.getClient()).thenReturn(mockClient);
        // Initialize the mock utils
        testMockUtils = new TestMockUtils(mockServer);
        // Initialize the reconciler
        EventGenerator eventGenerator = new EventGenerator(mockClient);
        MetricsService metricsService = new MetricsService(mockClient);
        RuleHandler ruleHandler = new RuleHandler(eventGenerator, metricsService, emailService);
        reconciler = new CostOptimizationRuleReconciler(ruleHandler);
    }

    // Test reconcile method with mocked context
    @Test
    void testReconcile() {
        String namespace = "test-namespace";
        String podName = "test-pod";
        String ruleName = "test-rule";
        int threshold = 80;

        // Create a CostOptimizationRule object
        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        ruleName, namespace, podName, MetricType.CPU, threshold);
        // Mock the kubernetes API endpoints
        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(namespace, podName, "100m", "128Mi");
        testMockUtils.mockPodUsageMetricsK8sApiEndpoints(namespace, podName, "50m", "64Mi");
        testMockUtils.mockEventsApiToEmptyResponse(namespace);

        // Call the reconcile method
        UpdateControl<CostOptimizationRule> updateControl = reconciler.reconcile(rule, context);

        // Verification
        assertNotNull(updateControl);
        assertTrue(updateControl.isPatchStatus());
    }

    // Test cleanup method with mocked context
    @Test
    void testCleanup() {
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule = new CostOptimizationRule();
        rule.getMetadata().setName("test-rule");
        rule.getMetadata().setNamespace("test-namespace");

        // Mock the context
        when(context.getClient()).thenReturn(mockClient);

        // Call the cleanup method
        DeleteControl deleteControl = reconciler.cleanup(rule, context);

        // Verification
        assertNotNull(deleteControl);
        assertTrue(EqualsBuilder.reflectionEquals(deleteControl, DeleteControl.defaultDelete()));
    }
}
