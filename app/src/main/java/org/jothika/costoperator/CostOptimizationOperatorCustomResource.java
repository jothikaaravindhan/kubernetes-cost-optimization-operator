package org.jothika.costoperator;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("org.jothika.costoperator")
@Version("v1")
public class CostOptimizationOperatorCustomResource
        extends CustomResource<CostOptimizationOperatorSpec, CostOptimizationOperatorStatus> implements Namespaced {

    private String name;
    private String namespace;
    private String podName;
    private String resourceType;
    private int threshold;
    private String notificationEmail;
    private String status;
}
