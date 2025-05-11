package org.jothika.costoperator.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import org.jothika.costoperator.TestMockUtils;
import org.jothika.costoperator.metrics.MetricType;
import org.jothika.costoperator.reconciler.CostOptimizationRule;
import org.jothika.costoperator.reconciler.enums.ThresholdCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient
class RuleValidatorTest {

    private KubernetesMockServer mockServer;
    private KubernetesClient mockClient;
    private TestMockUtils testMockUtils;
    private RuleValidator ruleValidator;

    @BeforeEach
    void setUp() {
        testMockUtils = new TestMockUtils(mockServer);
        ruleValidator = new RuleValidator(mockClient);
    }

    @Test
    void isValidRule() {
        String ruleName = "test-rule";
        String namespace = "default";
        String podName = "test-pod";
        String cpuAllocated = "100m";
        String memoryAllocated = "128Mi";

        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(
                namespace, podName, cpuAllocated, memoryAllocated);

        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        ruleName,
                        namespace,
                        podName,
                        MetricType.CPU,
                        ThresholdCondition.GREATERTHAN,
                        50);

        assertTrue(ruleValidator.isValidRule(rule));
    }

    @Test
    void isValidRuleFailure() {
        String ruleName = "test-rule";
        String namespace = "default";
        String podName = "test-pod";
        String cpuAllocated = "100m";
        String memoryAllocated = "128Mi";

        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(
                namespace, podName, cpuAllocated, memoryAllocated);

        CostOptimizationRule rule =
                testMockUtils.getCostOptimizationRule(
                        ruleName,
                        namespace,
                        "non-exist-pod",
                        MetricType.CPU,
                        ThresholdCondition.GREATERTHAN,
                        50);

        assertFalse(ruleValidator.isValidRule(rule));
    }
}
