package org.jothika.costoperator.handlers;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.jothika.costoperator.reconciler.CostOptimizationRule;
import org.springframework.stereotype.Service;

@Service
public class RuleValidator {

    KubernetesClient kubernetesClient;

    public RuleValidator(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public boolean isValidRule(CostOptimizationRule rule) {
        return isK8sResourceExists(rule);
    }

    boolean isK8sResourceExists(CostOptimizationRule rule) {
        // call k8s client to check pod exists in the namespace
        return kubernetesClient
                        .pods()
                        .inNamespace(rule.getMetadata().getNamespace())
                        .withName(rule.getSpec().getPodName())
                        .get()
                != null;
    }
}
