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
        return isValidEmail(rule) && isK8sResourceExists(rule);
    }

    boolean isValidEmail(CostOptimizationRule rule) {
        String email = rule.getSpec().getNotificationEmail();
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
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
