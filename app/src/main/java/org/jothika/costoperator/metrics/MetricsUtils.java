package org.jothika.costoperator.metrics;

import io.fabric8.kubernetes.client.KubernetesClient;
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
}
