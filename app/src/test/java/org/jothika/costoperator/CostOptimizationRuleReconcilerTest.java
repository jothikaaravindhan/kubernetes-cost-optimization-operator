package org.jothika.costoperator;

import static org.junit.jupiter.api.Assertions.*;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class CostOptimizationRuleReconcilerTest {

    @Mock Context<CostOptimizationRule> context;

    // Test reconcile method with mocked context
    @Test
    public void testReconcile() {
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule = new CostOptimizationRule();
        rule.getMetadata().setName("test-rule");
        rule.getMetadata().setNamespace("test-namespace");

        // Call the reconcile method
        CostOptimizationRuleReconciler reconciler = new CostOptimizationRuleReconciler();
        UpdateControl<CostOptimizationRule> updateControl = reconciler.reconcile(rule, context);

        // Verification
        assertNotNull(updateControl);
        assertTrue(updateControl.isNoUpdate());
    }

    // Test cleanup method with mocked context
    @Test
    public void testCleanup() {
        // Create a mock CostOptimizationRule object
        CostOptimizationRule rule = new CostOptimizationRule();
        rule.getMetadata().setName("test-rule");
        rule.getMetadata().setNamespace("test-namespace");

        // Call the cleanup method
        CostOptimizationRuleReconciler reconciler = new CostOptimizationRuleReconciler();
        DeleteControl deleteControl = reconciler.cleanup(rule, context);

        // Verification
        assertNotNull(deleteControl);
        assertTrue(EqualsBuilder.reflectionEquals(deleteControl, DeleteControl.defaultDelete()));
    }
}
