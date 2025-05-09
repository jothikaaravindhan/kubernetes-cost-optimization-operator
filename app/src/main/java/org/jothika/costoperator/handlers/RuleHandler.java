package org.jothika.costoperator.handlers;

import java.time.Instant;
import org.jothika.costoperator.CostOptimizationRule;
import org.jothika.costoperator.events.EventGenerator;
import org.jothika.costoperator.events.EventType;
import org.jothika.costoperator.metrics.MetricType;
import org.jothika.costoperator.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RuleHandler {

    private static final Logger log = LoggerFactory.getLogger(RuleHandler.class);
    EventGenerator eventGenerator;
    MetricsService metricsService;

    public RuleHandler(EventGenerator eventGenerator, MetricsService metricsService) {
        this.eventGenerator = eventGenerator;
        this.metricsService = metricsService;
    }

    public void reconcileRule(CostOptimizationRule costOptimizationRule) {
        Instant reconciliationStartTime = Instant.now();

        double metricUsagePercentage =
                metricsService.getMetricUsagePercentage(
                        costOptimizationRule.getMetadata().getNamespace(),
                        costOptimizationRule.getSpec().getPodName(),
                        MetricType.fromString(costOptimizationRule.getSpec().getResourceType()));
        String message;
        if (metricUsagePercentage < costOptimizationRule.getSpec().getThreshold()) {
            message =
                    String.format(
                            "Metric(%s) usage percentage: %s is less than threshold: %s. Notifying the user.",
                            costOptimizationRule.getSpec().getResourceType(),
                            metricUsagePercentage,
                            costOptimizationRule.getSpec().getThreshold());
            // send email to user

        } else {
            message =
                    String.format(
                            "Metric(%s) usage percentage: %s is greater than threshold: %s. No action needed.",
                            costOptimizationRule.getSpec().getResourceType(),
                            metricUsagePercentage,
                            costOptimizationRule.getSpec().getThreshold());
        }
        log.info(message);
        eventGenerator.generateEvent(
                costOptimizationRule,
                "Reconciliation at " + reconciliationStartTime,
                message,
                EventType.NORMAL);
    }
}
