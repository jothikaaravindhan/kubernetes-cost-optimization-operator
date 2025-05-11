package org.jothika.costoperator.reconciler.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ThresholdConditionTest {

    @Test
    void getType() {
        ThresholdCondition thresholdCondition = ThresholdCondition.GREATERTHAN;
        assertEquals(">", thresholdCondition.getCondition());
        thresholdCondition = ThresholdCondition.LESSTHAN;
        assertEquals("<", thresholdCondition.getCondition());
        thresholdCondition = ThresholdCondition.EQUALS;
        assertEquals("=", thresholdCondition.getCondition());
    }

    @Test
    void fromStringSuccess() {
        ThresholdCondition thresholdCondition = ThresholdCondition.fromString(">");
        assertEquals(ThresholdCondition.GREATERTHAN, thresholdCondition);
        thresholdCondition = ThresholdCondition.fromString("<");
        assertEquals(ThresholdCondition.LESSTHAN, thresholdCondition);
        thresholdCondition = ThresholdCondition.fromString("=");
        assertEquals(ThresholdCondition.EQUALS, thresholdCondition);
    }

    @Test
    void fromStringFailure() {
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            ThresholdCondition.fromString("unknown");
                        });
        String expectedMessage = "Unknown rule threshold condition: unknown";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
