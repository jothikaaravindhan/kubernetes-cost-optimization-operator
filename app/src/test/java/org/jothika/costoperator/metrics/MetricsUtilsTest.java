package org.jothika.costoperator.metrics;

import static org.junit.jupiter.api.Assertions.*;

import io.fabric8.kubernetes.api.model.APIServiceListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient
public class MetricsUtilsTest {

    KubernetesMockServer server;
    private KubernetesClient client;

    @Test
    @DisplayName("Should return true when metrics server is available")
    void testServerAvailableSuccess() {
        // Given
        server.expect()
                .get()
                .withPath("/apis/apiregistration.k8s.io/v1/apiservices/v1beta1.metrics.k8s.io")
                .andReturn(
                        HttpURLConnection.HTTP_OK,
                        new APIServiceListBuilder()
                                .addNewItem()
                                .withNewMetadata()
                                .withName("v1beta1.metrics.k8s.io")
                                .endMetadata()
                                .endItem()
                                .build())
                .once();

        // When
        MetricsUtils metricsUtils = new MetricsUtils(client);
        boolean metricsServerAvailable = metricsUtils.isMetricsServerAvailable();

        // Then
        assertTrue(metricsServerAvailable);
    }

    @Test
    @DisplayName("Should return false when metrics server is unavailable")
    void testServerAvailableFailure() {
        // When
        MetricsUtils metricsUtils = new MetricsUtils(client);
        boolean metricsServerAvailable = metricsUtils.isMetricsServerAvailable();

        // Then
        assertFalse(metricsServerAvailable);
    }
}
