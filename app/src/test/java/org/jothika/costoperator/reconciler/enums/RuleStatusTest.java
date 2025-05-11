package org.jothika.costoperator.reconciler.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RuleStatusTest {

    @Test
    void getType() {
        RuleStatus ruleStatus = RuleStatus.ACTIVE;
        assertEquals("Active", ruleStatus.getStatus());
        ruleStatus = RuleStatus.CREATED;
        assertEquals("Created", ruleStatus.getStatus());
        ruleStatus = RuleStatus.COMPLETED;
        assertEquals("Completed", ruleStatus.getStatus());
    }

    @Test
    void fromStringSuccess() {
        RuleStatus ruleStatus = RuleStatus.fromString("Created");
        assertEquals(RuleStatus.CREATED, ruleStatus);
        ruleStatus = RuleStatus.fromString("Active");
        assertEquals(RuleStatus.ACTIVE, ruleStatus);
        ruleStatus = RuleStatus.fromString("Completed");
        assertEquals(RuleStatus.COMPLETED, ruleStatus);
    }

    @Test
    void fromStringFailure() {
        Exception exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> {
                            RuleStatus.fromString("unknown");
                        });
        String expectedMessage = "Unknown rule status: unknown";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
