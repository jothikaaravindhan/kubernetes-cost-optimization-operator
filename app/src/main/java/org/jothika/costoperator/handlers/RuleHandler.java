package org.jothika.costoperator.handlers;

import java.time.Instant;
import org.jothika.costoperator.events.EventGenerator;
import org.jothika.costoperator.events.EventType;
import org.jothika.costoperator.mail.EmailService;
import org.jothika.costoperator.metrics.MetricType;
import org.jothika.costoperator.metrics.MetricsService;
import org.jothika.costoperator.reconciler.CostOptimizationRule;
import org.jothika.costoperator.reconciler.CostOptimizationRuleStatus;
import org.jothika.costoperator.reconciler.enums.RuleStatus;
import org.jothika.costoperator.reconciler.enums.ThresholdCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RuleHandler {

    private static final Logger log = LoggerFactory.getLogger(RuleHandler.class);
    EventGenerator eventGenerator;
    MetricsService metricsService;
    EmailService emailService;
    RuleValidator ruleValidator;

    public RuleHandler(
            EventGenerator eventGenerator,
            MetricsService metricsService,
            EmailService emailService,
            RuleValidator ruleValidator) {
        this.eventGenerator = eventGenerator;
        this.metricsService = metricsService;
        this.emailService = emailService;
        this.ruleValidator = ruleValidator;
    }

    public CostOptimizationRule reconcileRule(CostOptimizationRule costOptimizationRule) {
        Instant reconciliationStartTime = Instant.now();
        if (costOptimizationRule.getStatus() == null) {
            CostOptimizationRuleStatus costOptimizationRuleStatus =
                    new CostOptimizationRuleStatus();
            costOptimizationRuleStatus.setRuleStatus(RuleStatus.CREATED);
            costOptimizationRule.setStatus(costOptimizationRuleStatus);
        } else if (costOptimizationRule.getStatus().getRuleStatus().equals(RuleStatus.COMPLETED)
                || costOptimizationRule.getStatus().getRuleStatus().equals(RuleStatus.FAILED)) {
            log.info(
                    "Rule {} is in {} state. Skipping reconciliation.",
                    costOptimizationRule.getMetadata().getName(),
                    costOptimizationRule.getStatus().getRuleStatus());
            return costOptimizationRule;
        }
        if (ruleValidator.isValidRule(costOptimizationRule)) {
            return startReconciliation(costOptimizationRule, reconciliationStartTime);
        } else {
            String message =
                    String.format(
                            "Pod %s in namespace %s does not exist. Skipping reconciliation.",
                            costOptimizationRule.getSpec().getPodName(),
                            costOptimizationRule.getMetadata().getNamespace());
            log.warn(message);
            eventGenerator.generateEvent(
                    costOptimizationRule,
                    "Reconciliation at " + reconciliationStartTime,
                    message,
                    EventType.WARNING);
            CostOptimizationRuleStatus costOptimizationRuleStatus =
                    new CostOptimizationRuleStatus();
            costOptimizationRuleStatus.setRuleStatus(RuleStatus.FAILED);
            costOptimizationRule.setStatus(costOptimizationRuleStatus);
            return costOptimizationRule;
        }
    }

    CostOptimizationRule startReconciliation(
            CostOptimizationRule costOptimizationRule, Instant reconciliationStartTime) {
        double metricUsagePercentage =
                metricsService.getMetricUsagePercentage(
                        costOptimizationRule.getMetadata().getNamespace(),
                        costOptimizationRule.getSpec().getPodName(),
                        MetricType.fromString(costOptimizationRule.getSpec().getResourceType()));
        String message;
        String reconciliationAt = "Reconciliation at ";
        if (isThresholdCrossed(costOptimizationRule, metricUsagePercentage)) {
            message = getThresholdEventMessage(costOptimizationRule, metricUsagePercentage);
            eventGenerator.generateEvent(
                    costOptimizationRule,
                    reconciliationAt + reconciliationStartTime,
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
            costOptimizationRule.getStatus().setRuleStatus(RuleStatus.COMPLETED);
            return costOptimizationRule;
        } else {
            message = getThresholdEventMessage(costOptimizationRule, metricUsagePercentage);
            log.info(message);
            eventGenerator.generateEvent(
                    costOptimizationRule,
                    reconciliationAt + reconciliationStartTime,
                    message,
                    EventType.NORMAL);
            costOptimizationRule.getStatus().setRuleStatus(RuleStatus.ACTIVE);
            return costOptimizationRule;
        }
    }

    boolean isThresholdCrossed(
            CostOptimizationRule costOptimizationRule, double metricUsagePercentage) {
        return switch (costOptimizationRule.getSpec().getThresholdCondition()) {
            case ThresholdCondition.GREATERTHAN ->
                    metricUsagePercentage > costOptimizationRule.getSpec().getThreshold();
            case ThresholdCondition.LESSTHAN ->
                    metricUsagePercentage < costOptimizationRule.getSpec().getThreshold();
            case ThresholdCondition.EQUALS ->
                    metricUsagePercentage == costOptimizationRule.getSpec().getThreshold();
        };
    }

    String getThresholdEventMessage(
            CostOptimizationRule costOptimizationRule, double metricUsagePercentage) {
        if (isThresholdCrossed(costOptimizationRule, metricUsagePercentage)) {
            return switch (costOptimizationRule.getSpec().getThresholdCondition()) {
                case GREATERTHAN ->
                        String.format(
                                "Metric(%s) usage percentage: %s is greater than threshold: %s. Notifying the user.",
                                costOptimizationRule.getSpec().getResourceType(),
                                metricUsagePercentage,
                                costOptimizationRule.getSpec().getThreshold());
                case LESSTHAN ->
                        String.format(
                                "Metric(%s) usage percentage: %s is less than threshold: %s. Notifying the user.",
                                costOptimizationRule.getSpec().getResourceType(),
                                metricUsagePercentage,
                                costOptimizationRule.getSpec().getThreshold());
                case EQUALS ->
                        String.format(
                                "Metric(%s) usage percentage: %s is equal to threshold: %s. Notifying the user.",
                                costOptimizationRule.getSpec().getResourceType(),
                                metricUsagePercentage,
                                costOptimizationRule.getSpec().getThreshold());
            };
        } else {
            return switch (costOptimizationRule.getSpec().getThresholdCondition()) {
                case GREATERTHAN ->
                        String.format(
                                "Metric(%s) usage percentage: %s is less than threshold: %s. No action needed.",
                                costOptimizationRule.getSpec().getResourceType(),
                                metricUsagePercentage,
                                costOptimizationRule.getSpec().getThreshold());
                case LESSTHAN ->
                        String.format(
                                "Metric(%s) usage percentage: %s is greater than threshold: %s. No action needed.",
                                costOptimizationRule.getSpec().getResourceType(),
                                metricUsagePercentage,
                                costOptimizationRule.getSpec().getThreshold());
                case EQUALS ->
                        String.format(
                                "Metric(%s) usage percentage: %s is not equal to threshold: %s. No action needed.",
                                costOptimizationRule.getSpec().getResourceType(),
                                metricUsagePercentage,
                                costOptimizationRule.getSpec().getThreshold());
            };
        }
    }
}
