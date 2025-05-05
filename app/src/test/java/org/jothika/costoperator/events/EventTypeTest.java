package org.jothika.costoperator.events;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventTypeTest {

    @Test
    void getType() {
        EventType eventType = EventType.NORMAL;
        assertEquals("normal", eventType.getType());
        eventType = EventType.WARNING;
        assertEquals("warning", eventType.getType());
    }

    @Test
    void fromStringSuccess() {
        EventType eventType = EventType.fromString("normal");
        assertEquals(EventType.NORMAL, eventType);
        eventType = EventType.fromString("warning");
        assertEquals(EventType.WARNING, eventType);
    }

    @Test
    void fromStringFailure() {
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            EventType.fromString("unknown");
                        });
        String expectedMessage = "Unknown event type: unknown";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
