package org.jothika.costoperator;

import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;

@ControllerConfiguration
public class CostOptimizationOperatorReconciler implements Reconciler<CostOptimizationOperatorCustomResource> {
    private static final Logger log = LoggerFactory.getLogger(CostOptimizationOperatorReconciler.class);

    public UpdateControl<CostOptimizationOperatorCustomResource> reconcile(
            CostOptimizationOperatorCustomResource primary,
            Context<CostOptimizationOperatorCustomResource> context) {
        log.info("Reconciling CostOptimizationOperatorCustomResource: {}", primary.getMetadata().getName());
        log.info("Namespace: {}", primary.getMetadata().getNamespace());
        return UpdateControl.noUpdate();
    }

    public DeleteControl cleanup(CostOptimizationOperatorCustomResource primary,
            Context<CostOptimizationOperatorCustomResource> context) {
        log.info("Cleaning up CostOptimizationOperatorCustomResource: {}", primary.getMetadata().getName());
        log.info("Namespace: {}", primary.getMetadata().getNamespace());
        return DeleteControl.defaultDelete();
    }
}
