package org.jothika.costoperator.metrics;

public enum MetricType {
    CPU("cpu"),
    MEMORY("memory");

    private final String type;

    MetricType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static MetricType fromString(String type) {
        for (MetricType metricType : MetricType.values()) {
            if (metricType.type.equalsIgnoreCase(type)) {
                return metricType;
            }
        }
        throw new IllegalArgumentException("Unknown metric type: " + type);
    }
}
