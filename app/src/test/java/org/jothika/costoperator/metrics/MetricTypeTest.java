package org.jothika.costoperator.metrics;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetricTypeTest {

    @Test
    void getType() {
        MetricType metricType = MetricType.CPU;
        assertEquals("cpu", metricType.getType());
        metricType = MetricType.MEMORY;
        assertEquals("memory", metricType.getType());
    }

    @Test
    void fromStringSuccess() {
        MetricType metricType = MetricType.fromString("memory");
        assertEquals(MetricType.MEMORY, metricType);
        metricType = MetricType.fromString("cpu");
        assertEquals(MetricType.CPU, metricType);
    }

    @Test
    void fromStringFailure() {
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            MetricType.fromString("unknown");
                        });
        String expectedMessage = "Unknown metric type: unknown";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
