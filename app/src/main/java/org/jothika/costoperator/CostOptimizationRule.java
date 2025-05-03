package org.jothika.costoperator;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("org.jothika.costoperator")
@Version("v1")
public class CostOptimizationRule
        extends CustomResource<CostOptimizationRuleSpec, CostOptimizationRuleStatus>
        implements Namespaced {}
