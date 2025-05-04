package org.jothika.costoperator;

import io.fabric8.crd.generator.annotation.PrinterColumn;

public class CostOptimizationRuleSpec {

    @PrinterColumn(name = "Pod Name")
    private String podName;

    @PrinterColumn(name = "Resource Type")
    private String resourceType;

    @PrinterColumn(name = "Threshold")
    private int threshold;

    private String notificationEmail;

    @PrinterColumn(name = "Status")
    private String status;

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

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
