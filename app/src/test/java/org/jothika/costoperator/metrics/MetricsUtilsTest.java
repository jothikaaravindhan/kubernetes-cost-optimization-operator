package org.jothika.costoperator.metrics;

import static org.junit.jupiter.api.Assertions.*;

import io.fabric8.kubernetes.api.model.APIServiceListBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import java.net.HttpURLConnection;
import org.jothika.costoperator.TestMockUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@EnableKubernetesMockClient
public class MetricsUtilsTest {

    KubernetesMockServer mockServer;
    private KubernetesClient client;
    private TestMockUtils testMockUtils;

    @BeforeEach
    void setUp() {
        // Setup default mock behavior
        testMockUtils = new TestMockUtils(mockServer);
    }

    @Test
    @DisplayName("Should return true when metrics server is available")
    void testServerAvailableSuccess() {
        // Given
        mockServer
                .expect()
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

    // Test for metrics success cpu and memory metrics type
    @Test
    @DisplayName("Should return usage metrics for CPU and MEMORY")
    void testGetUsageMetricsSuccess() {
        // Given
        String namespace = "default";
        String podName = "test-pod";
        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(namespace, podName, "100m", "128Mi");
        testMockUtils.mockPodUsageMetricsK8sApiEndpoints(namespace, podName, "25m", "64Mi");

        // When
        MetricsUtils metricsUtils = new MetricsUtils(client);
        double cpuUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.CPU);
        double memoryUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.MEMORY);

        // Then
        assertEquals(25.0, cpuUsageMetricsPercentage);
        assertEquals(50.0, memoryUsageMetricsPercentage);
    }

    // Test for metrics failure when pod metrics is null
    @Test
    @DisplayName("Should return 0 when pod metrics is null")
    void testGetMetricsFailureWhenPodMetricsIsNull() {
        // Given
        String namespace = "default";
        String podName = "test-pod";
        testMockUtils.mockPodUsageMetricsToNullK8sApiEndpoints(namespace, podName);
        testMockUtils.mockPodAllocatedMetricsToNullK8sApiEndpoints(namespace, podName);

        // When
        MetricsUtils metricsUtils = new MetricsUtils(client);
        double cpuUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.CPU);
        double memoryUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.MEMORY);

        // Then
        assertEquals(0.0, cpuUsageMetricsPercentage);
        assertEquals(0.0, memoryUsageMetricsPercentage);
    }

    // Test for metrics failure when pod metrics containers is empty
    @Test
    @DisplayName("Should return 0 when pod metrics containers is empty")
    void testGetMetricsFailureWhenPodMetricsContainersIsEmpty() {
        // Given
        String namespace = "default";
        String podName = "test-pod";
        testMockUtils.mockPodUsageMetricsToEmptyK8sApiEndpoints(namespace, podName);
        testMockUtils.mockPodAllocatedMetricsToEmptyK8sApiEndpoints(namespace, podName);

        // When
        MetricsUtils metricsUtils = new MetricsUtils(client);
        double cpuUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.CPU);
        double memoryUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.MEMORY);

        // Then
        assertEquals(0.0, cpuUsageMetricsPercentage);
        assertEquals(0.0, memoryUsageMetricsPercentage);
    }

    // Test for metrics failure when pod usage metrics is null
    @Test
    @DisplayName("Should return 0 when pod metrics containers is empty")
    void testGetMetricsFailureWhenPodUsageMetricsIsNull() {
        // Given
        String namespace = "default";
        String podName = "test-pod";
        testMockUtils.mockPodUsageMetricsToNullK8sApiEndpoints(namespace, podName);
        testMockUtils.mockPodAllocatedMetricsK8sApiEndpoints(namespace, podName, "100m", "128Mi");

        // When
        MetricsUtils metricsUtils = new MetricsUtils(client);
        double cpuUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.CPU);
        double memoryUsageMetricsPercentage =
                metricsUtils.getMetricUsagePercentage(namespace, podName, MetricType.MEMORY);

        // Then
        assertEquals(0.0, cpuUsageMetricsPercentage);
        assertEquals(0.0, memoryUsageMetricsPercentage);
    }
}
