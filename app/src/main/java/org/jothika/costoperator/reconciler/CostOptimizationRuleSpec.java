package org.jothika.costoperator.reconciler;

import io.fabric8.crd.generator.annotation.PrinterColumn;
import org.jothika.costoperator.reconciler.enums.ThresholdCondition;

public class CostOptimizationRuleSpec {

    @PrinterColumn(name = "Pod Name")
    private String podName;

    @PrinterColumn(name = "Resource Type")
    private String resourceType;

    @PrinterColumn(name = "Condition")
    private ThresholdCondition thresholdCondition;

    @PrinterColumn(name = "Threshold")
    private double threshold;

    private String notificationEmail;

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public ThresholdCondition getThresholdCondition() {
        return thresholdCondition;
    }

    public void setThresholdCondition(ThresholdCondition thresholdCondition) {
        this.thresholdCondition = thresholdCondition;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }
}
