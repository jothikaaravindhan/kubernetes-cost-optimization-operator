package org.jothika.costoperator;

public enum RuleStatus {
    CREATED("Created"),
    ACTIVE("Active"),
    COMPLETED("Completed");

    private final String status;

    RuleStatus(String status) {
        this.status = status;
    }

    public static RuleStatus fromString(String status) {
        for (RuleStatus ruleStatusType : RuleStatus.values()) {
            if (ruleStatusType.status.equalsIgnoreCase(status)) {
                return ruleStatusType;
            }
        }
        throw new IllegalArgumentException("Unknown rule status: " + status);
    }

    public String getStatus() {
        return status;
    }
}
