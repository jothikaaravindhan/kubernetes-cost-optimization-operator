package org.jothika.costoperator.events;

public enum EventType {
    NORMAL("normal"),
    WARNING("warning");

    private final String type;

    EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EventType fromString(String type) {
        for (EventType metricType : EventType.values()) {
            if (metricType.type.equalsIgnoreCase(type)) {
                return metricType;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + type);
    }
}
