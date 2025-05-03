package org.jothika.costoperator;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.Operator;
import java.util.concurrent.TimeUnit;
import org.jothika.costoperator.metrics.MetricsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) {
        Operator operator = new Operator();
        KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();
        MetricsUtils metricsUtils = new MetricsUtils(kubernetesClient);
        // Check continuously if the metrics server is installed and available
        // if installed then start the operator
        while (!metricsUtils.isMetricsServerAvailable()) {
            log.info("Metrics server is not installed. Waiting for 5 seconds...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error while waiting for metrics server", e);
            }
        }
        log.info("Metrics Server is available. Starting Cost Optimization Operator...");
        operator.register(new CostOptimizationRuleReconciler());
        operator.start();
        log.info("Operator started.");
    }
}
