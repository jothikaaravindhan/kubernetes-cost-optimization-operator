package org.jothika.costoperator;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration
public class CostOptimizationRuleReconciler implements Reconciler<CostOptimizationRule> {

    private static final Logger log = LoggerFactory.getLogger(CostOptimizationRuleReconciler.class);

    public UpdateControl<CostOptimizationRule> reconcile(
            CostOptimizationRule primary, Context<CostOptimizationRule> context) {
        log.info(
                "Reconciling CostOptimizationOperatorCustomResource: {}",
                primary.getMetadata().getName());
        log.info("Namespace: {}", primary.getMetadata().getNamespace());
        return UpdateControl.noUpdate();
    }

    public DeleteControl cleanup(
            CostOptimizationRule primary, Context<CostOptimizationRule> context) {
        log.info(
                "Cleaning up CostOptimizationOperatorCustomResource: {}",
                primary.getMetadata().getName());
        log.info("Namespace: {}", primary.getMetadata().getNamespace());
        return DeleteControl.defaultDelete();
    }
}
