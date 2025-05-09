package org.jothika.costoperator;

import io.fabric8.crd.generator.annotation.PrinterColumn;

public class CostOptimizationRuleStatus {

    @PrinterColumn(name = "Status")
    private String ruleStatus;

    // getter and setter for ruleStatus
    public String getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(String ruleStatus) {
        this.ruleStatus = ruleStatus;
    }
}
