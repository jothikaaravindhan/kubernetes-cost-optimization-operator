package org.jothika.costoperator;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.ContainerMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.ContainerMetricsBuilder;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetricsBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import org.jothika.costoperator.metrics.MetricType;

public class TestMockUtils {

    private final KubernetesMockServer mockServer;

    public TestMockUtils(KubernetesMockServer mockServer) {
        this.mockServer = mockServer;
    }

    public CostOptimizationRule getCostOptimizationRule(
            String ruleName,
            String namespace,
            String podName,
            MetricType resourceType,
            int threshold) {
        CostOptimizationRule rule = new CostOptimizationRule();
        rule.getMetadata().setName(ruleName);
        rule.getMetadata().setNamespace(namespace);
        CostOptimizationRuleSpec costOptimizationRuleSpec = new CostOptimizationRuleSpec();
        costOptimizationRuleSpec.setPodName(podName);
        costOptimizationRuleSpec.setResourceType(resourceType.name());
        costOptimizationRuleSpec.setThreshold(threshold);
        rule.setSpec(costOptimizationRuleSpec);
        return rule;
    }

    public void mockPodAllocatedMetricsK8sApiEndpoints(
            String namespace, String podName, String cpuAllocated, String memoryAllocated) {
        ResourceRequirements resourceRequirements =
                new ResourceRequirementsBuilder()
                        .withRequests(
                                Map.of(
                                        MetricType.CPU.getType(),
                                        new Quantity(cpuAllocated),
                                        MetricType.MEMORY.getType(),
                                        new Quantity(memoryAllocated)))
                        .build();
        Container container =
                new ContainerBuilder()
                        .withName(podName)
                        .withResources(resourceRequirements)
                        .build();
        PodSpec podSpec = new PodSpecBuilder().withContainers(List.of(container)).build();

        Pod pod =
                new PodBuilder()
                        .withSpec(podSpec)
                        .withMetadata(
                                new ObjectMetaBuilder()
                                        .withName(podName)
                                        .withNamespace(namespace)
                                        .build())
                        .build();

        mockServer
                .expect()
                .get()
                .withPath(String.format("/api/v1/namespaces/%s/pods/%s", namespace, podName))
                .andReturn(HttpURLConnection.HTTP_OK, pod)
                .always();
    }

    public void mockPodUsageMetricsK8sApiEndpoints(
            String namespace, String podName, String cpuUsage, String memoryUsage) {
        ContainerMetrics containerMetrics =
                new ContainerMetricsBuilder()
                        .withName(podName)
                        .withUsage(
                                Map.of(
                                        MetricType.CPU.getType(),
                                        new Quantity(cpuUsage),
                                        MetricType.MEMORY.getType(),
                                        new Quantity(memoryUsage)))
                        .build();
        PodMetrics podMetrics =
                new PodMetricsBuilder().withContainers(List.of(containerMetrics)).build();
        mockServer
                .expect()
                .get()
                .withPath(
                        String.format(
                                "/apis/metrics.k8s.io/v1beta1/namespaces/%s/pods/%s",
                                namespace, podName))
                .andReturn(HttpURLConnection.HTTP_OK, podMetrics)
                .always();
    }

    public void mockPodAllocatedMetricsToNullK8sApiEndpoints(String namespace, String podName) {
        Pod pod =
                new PodBuilder()
                        .withMetadata(
                                new ObjectMetaBuilder()
                                        .withName(podName)
                                        .withNamespace(namespace)
                                        .build())
                        .build();

        mockServer
                .expect()
                .get()
                .withPath(String.format("/api/v1/namespaces/%s/pods/%s", namespace, podName))
                .andReturn(HttpURLConnection.HTTP_OK, pod)
                .always();
    }

    public void mockPodUsageMetricsToNullK8sApiEndpoints(String namespace, String podName) {
        PodMetrics podMetrics = new PodMetricsBuilder().build();
        mockServer
                .expect()
                .get()
                .withPath(
                        String.format(
                                "/apis/metrics.k8s.io/v1beta1/namespaces/%s/pods/%s",
                                namespace, podName))
                .andReturn(HttpURLConnection.HTTP_OK, podMetrics)
                .always();
    }

    public void mockPodAllocatedMetricsToEmptyK8sApiEndpoints(String namespace, String podName) {
        PodSpec podSpec = new PodSpecBuilder().withContainers(List.of()).build();

        Pod pod =
                new PodBuilder()
                        .withSpec(podSpec)
                        .withMetadata(
                                new ObjectMetaBuilder()
                                        .withName(podName)
                                        .withNamespace(namespace)
                                        .build())
                        .build();

        mockServer
                .expect()
                .get()
                .withPath(String.format("/api/v1/namespaces/%s/pods/%s", namespace, podName))
                .andReturn(HttpURLConnection.HTTP_OK, pod)
                .always();
    }

    public void mockPodUsageMetricsToEmptyK8sApiEndpoints(String namespace, String podName) {
        PodMetrics podMetrics = new PodMetricsBuilder().withContainers(List.of()).build();
        mockServer
                .expect()
                .get()
                .withPath(
                        String.format(
                                "/apis/metrics.k8s.io/v1beta1/namespaces/%s/pods/%s",
                                namespace, podName))
                .andReturn(HttpURLConnection.HTTP_OK, podMetrics)
                .always();
    }

    public void mockEventsApiToEmptyResponse(String namespace) {
        mockServer
                .expect()
                .post()
                .withPath(String.format("/api/v1/namespaces/%s/events", namespace))
                .andReturn(HttpURLConnection.HTTP_OK, "{}")
                .once();
    }
}
