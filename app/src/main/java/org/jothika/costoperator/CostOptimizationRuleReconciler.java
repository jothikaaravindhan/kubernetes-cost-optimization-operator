package org.jothika.costoperator;

import io.javaoperatorsdk.operator.api.reconciler.Cleaner;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.MaxReconciliationInterval;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import java.util.concurrent.TimeUnit;
import org.jothika.costoperator.handlers.RuleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ControllerConfiguration(
        maxReconciliationInterval =
                @MaxReconciliationInterval(interval = 10, timeUnit = TimeUnit.SECONDS))
@Component
public class CostOptimizationRuleReconciler
        implements Reconciler<CostOptimizationRule>, Cleaner<CostOptimizationRule> {

    private static final Logger log = LoggerFactory.getLogger(CostOptimizationRuleReconciler.class);
    private final RuleHandler ruleHandler;

    public CostOptimizationRuleReconciler(RuleHandler ruleHandler) {
        this.ruleHandler = ruleHandler;
    }

    public UpdateControl<CostOptimizationRule> reconcile(
            CostOptimizationRule primary, Context<CostOptimizationRule> context) {
        log.debug(
                "Reconciling {}.{}",
                primary.getMetadata().getNamespace(),
                primary.getMetadata().getName());
        ruleHandler.reconcileRule(primary);
        return UpdateControl.patchStatus(primary);
    }

    @Override
    public DeleteControl cleanup(
            CostOptimizationRule primary, Context<CostOptimizationRule> context) {
        log.debug(
                "Cleaning up {}.{}",
                primary.getMetadata().getNamespace(),
                primary.getMetadata().getName());
        return DeleteControl.defaultDelete();
    }
}
