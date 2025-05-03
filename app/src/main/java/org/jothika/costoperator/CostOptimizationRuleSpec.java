package org.jothika.costoperator;

import io.fabric8.crd.generator.annotation.PrinterColumn;

public class CostOptimizationRuleSpec {
    @PrinterColumn private String name;
    @PrinterColumn private String namespace;
    @PrinterColumn private String podName;
    @PrinterColumn private String resourceType;
    @PrinterColumn private int threshold;
    private String notificationEmail;
    @PrinterColumn private String status;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

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
