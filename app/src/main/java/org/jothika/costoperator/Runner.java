package org.jothika.costoperator;

import io.javaoperatorsdk.operator.Operator;
import java.util.concurrent.TimeUnit;
import org.jothika.costoperator.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Runner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);
    private final MetricsService metricsService;
    private final CostOptimizationRuleReconciler ruleReconciler;

    public Runner(MetricsService metricsService, CostOptimizationRuleReconciler ruleReconciler) {
        this.metricsService = metricsService;
        this.ruleReconciler = ruleReconciler;
    }

    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Operator operator = new Operator();
        // Check continuously if the metrics server is installed and available
        // if installed then start the operator
        while (!metricsService.isMetricsServerAvailable()) {
            log.info("Metrics server is not installed. Waiting for 5 seconds...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error while waiting for metrics server", e);
            }
        }
        log.info("Metrics Server is available. Starting Cost Optimization Operator...");
        operator.register(ruleReconciler);
        operator.start();
        log.info("Operator started.");
    }
}
