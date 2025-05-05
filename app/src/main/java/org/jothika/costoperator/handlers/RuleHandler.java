package org.jothika.costoperator.handlers;

import io.fabric8.kubernetes.client.KubernetesClient;
import java.time.Instant;
import org.jothika.costoperator.CostOptimizationRule;
import org.jothika.costoperator.events.EventGenerator;
import org.jothika.costoperator.events.EventType;
import org.jothika.costoperator.metrics.MetricType;
import org.jothika.costoperator.metrics.MetricsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleHandler {

    private static final Logger log = LoggerFactory.getLogger(RuleHandler.class);
    CostOptimizationRule costOptimizationRule;
    KubernetesClient kubernetesClient;
    EventGenerator eventGenerator;
    MetricsUtils metricsUtils;

    public RuleHandler(
            CostOptimizationRule costOptimizationRule, KubernetesClient kubernetesClient) {
        this.costOptimizationRule = costOptimizationRule;
        this.kubernetesClient = kubernetesClient;
        this.eventGenerator = new EventGenerator(kubernetesClient);
        this.metricsUtils = new MetricsUtils(kubernetesClient);
    }

    public void reconcileRule() {
        Instant reconciliationStartTime = Instant.now();
        double metricUsagePercentage =
                metricsUtils.getMetricUsagePercentage(
                        costOptimizationRule.getMetadata().getNamespace(),
                        costOptimizationRule.getSpec().getPodName(),
                        MetricType.fromString(costOptimizationRule.getSpec().getResourceType()));
        String message =
                String.format(
                        "Metric(%s) usage percentage: %s",
                        costOptimizationRule.getSpec().getResourceType(), metricUsagePercentage);
        log.info(message);
        eventGenerator.generateEvent(
                costOptimizationRule,
                "Reconciliation at " + reconciliationStartTime,
                message,
                EventType.NORMAL);
    }
}
