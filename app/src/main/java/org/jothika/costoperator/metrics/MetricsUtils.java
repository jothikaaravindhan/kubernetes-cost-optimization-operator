package org.jothika.costoperator.metrics;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsUtils {

    private static final Logger log = LoggerFactory.getLogger(MetricsUtils.class);
    private final KubernetesClient kubernetesClient;

    public MetricsUtils(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public boolean isMetricsServerAvailable() {
        // Check if the metrics server is available
        return kubernetesClient.apiServices().withName("v1beta1.metrics.k8s.io").get() != null;
    }

    public double getMetricUsagePercentage(
            String namespace, String podName, MetricType metricType) {
        BigDecimal allocatedMetrics = getAllocatedMetrics(podName, namespace, metricType);
        BigDecimal usageMetrics = getUsageMetrics(podName, namespace, metricType);
        if (allocatedMetrics.compareTo(BigDecimal.ZERO) == 0) {
            log.warn(
                    "Allocated metrics({}) are zero for pod: {} in namespace: {}",
                    metricType.name(),
                    podName,
                    namespace);
            return 0.0;
        }
        if (usageMetrics.compareTo(BigDecimal.ZERO) == 0) {
            log.warn(
                    "Usage metrics({}) are zero for pod: {} in namespace: {}",
                    metricType.name(),
                    podName,
                    namespace);
            return 0.0;
        }
        log.debug(
                "Usage metrics({}) for pod: {} in namespace: {} is {}",
                metricType.name(),
                podName,
                namespace,
                usageMetrics.doubleValue());
        log.debug(
                "Allocated metrics({}) for pod: {} in namespace: {} is {}",
                metricType.name(),
                podName,
                namespace,
                allocatedMetrics.doubleValue());
        // Calculate the percentage
        return (usageMetrics.doubleValue() / allocatedMetrics.doubleValue()) * 100;
    }

    private BigDecimal getAllocatedMetrics(
            String podName, String namespace, MetricType metricType) {
        log.debug("Fetching allocated metrics for pod: {} in namespace: {}", podName, namespace);
        Pod podToGetMetrics =
                kubernetesClient.pods().inNamespace(namespace).withName(podName).get();

        if (podToGetMetrics == null) {
            log.warn("Pod not found: {} in namespace: {}", podName, namespace);
            return BigDecimal.ZERO;
        }
        return switch (metricType) {
            case CPU ->
                    podToGetMetrics.getSpec().getContainers().stream()
                            .map(
                                    c ->
                                            c.getResources()
                                                    .getRequests()
                                                    .get("cpu")
                                                    .getNumericalAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            case MEMORY ->
                    podToGetMetrics.getSpec().getContainers().stream()
                            .map(
                                    c ->
                                            c.getResources()
                                                    .getRequests()
                                                    .get("memory")
                                                    .getNumericalAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        };
    }

    private BigDecimal getUsageMetrics(String podName, String namespace, MetricType metricType) {
        log.debug(
                "Fetching usage metrics({}) for pod: {} in namespace: {}",
                metricType.name(),
                podName,
                namespace);
        PodMetrics podMetrics =
                kubernetesClient.top().pods().inNamespace(namespace).withName(podName).metric();

        if (podMetrics == null || podMetrics.getContainers().isEmpty()) {
            log.warn(
                    "No metrics({}) found for pod: {} in namespace: {}",
                    metricType.name(),
                    podName,
                    namespace);
            return BigDecimal.ZERO;
        }

        return switch (metricType) {
            case CPU ->
                    podMetrics.getContainers().stream()
                            .map(c -> c.getUsage().get("cpu").getNumericalAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            case MEMORY ->
                    podMetrics.getContainers().stream()
                            .map(c -> c.getUsage().get("memory").getNumericalAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        };
    }
}
