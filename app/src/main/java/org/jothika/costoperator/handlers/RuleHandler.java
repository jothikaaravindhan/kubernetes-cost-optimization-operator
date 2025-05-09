package org.jothika.costoperator.handlers;

import java.time.Instant;
import org.jothika.costoperator.CostOptimizationRule;
import org.jothika.costoperator.CostOptimizationRuleStatus;
import org.jothika.costoperator.RuleStatus;
import org.jothika.costoperator.events.EventGenerator;
import org.jothika.costoperator.events.EventType;
import org.jothika.costoperator.mail.EmailService;
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
    EmailService emailService;

    public RuleHandler(
            EventGenerator eventGenerator,
            MetricsService metricsService,
            EmailService emailService) {
        this.eventGenerator = eventGenerator;
        this.metricsService = metricsService;
        this.emailService = emailService;
    }

    public CostOptimizationRule reconcileRule(CostOptimizationRule costOptimizationRule) {
        Instant reconciliationStartTime = Instant.now();
        if (costOptimizationRule.getStatus() == null) {
            CostOptimizationRuleStatus costOptimizationRuleStatus =
                    new CostOptimizationRuleStatus();
            costOptimizationRuleStatus.setRuleStatus(RuleStatus.CREATED.getStatus());
            costOptimizationRule.setStatus(costOptimizationRuleStatus);
        } else if (costOptimizationRule
                .getStatus()
                .getRuleStatus()
                .equals(RuleStatus.COMPLETED.getStatus())) {
            log.info(
                    "Rule {} is in COMPLETED state. Skipping reconciliation.",
                    costOptimizationRule.getMetadata().getName());
            return costOptimizationRule;
        }
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
            eventGenerator.generateEvent(
                    costOptimizationRule,
                    "Reconciliation at " + reconciliationStartTime,
                    message,
                    EventType.NORMAL);
            log.info(message);
            // send email to user
            emailService.sendAlertOnPodThreshold(costOptimizationRule, metricUsagePercentage);
            eventGenerator.generateEvent(
                    costOptimizationRule,
                    "Email notification sent at " + reconciliationStartTime,
                    message,
                    EventType.NORMAL);
            costOptimizationRule.getStatus().setRuleStatus(RuleStatus.COMPLETED.getStatus());
            return costOptimizationRule;
        } else {
            message =
                    String.format(
                            "Metric(%s) usage percentage: %s is greater than threshold: %s. No action needed.",
                            costOptimizationRule.getSpec().getResourceType(),
                            metricUsagePercentage,
                            costOptimizationRule.getSpec().getThreshold());
            log.info(message);
            eventGenerator.generateEvent(
                    costOptimizationRule,
                    "Reconciliation at " + reconciliationStartTime,
                    message,
                    EventType.NORMAL);
            costOptimizationRule.getStatus().setRuleStatus(RuleStatus.ACTIVE.getStatus());
            return costOptimizationRule;
        }
    }
}
