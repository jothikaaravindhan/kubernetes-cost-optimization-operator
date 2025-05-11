package org.jothika.costoperator.reconciler;

import io.fabric8.crd.generator.annotation.PrinterColumn;
import org.jothika.costoperator.reconciler.enums.RuleStatus;

public class CostOptimizationRuleStatus {

    @PrinterColumn(name = "Status")
    private RuleStatus ruleStatus;

    // getter and setter for ruleStatus
    public RuleStatus getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(RuleStatus ruleStatus) {
        this.ruleStatus = ruleStatus;
    }
}
