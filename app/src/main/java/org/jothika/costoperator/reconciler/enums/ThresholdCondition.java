package org.jothika.costoperator.reconciler.enums;

public enum ThresholdCondition {
    GREATERTHAN(">"),
    LESSTHAN("<"),
    EQUALS("=");

    private final String condition;

    ThresholdCondition(String condition) {
        this.condition = condition;
    }

    public static ThresholdCondition fromString(String condition) {
        for (ThresholdCondition thresholdCondition : ThresholdCondition.values()) {
            if (thresholdCondition.condition.equalsIgnoreCase(condition)) {
                return thresholdCondition;
            }
        }
        throw new IllegalArgumentException("Unknown rule threshold condition: " + condition);
    }

    public String getCondition() {
        return condition;
    }
}
