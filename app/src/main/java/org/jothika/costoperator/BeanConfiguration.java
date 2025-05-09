package org.jothika.costoperator;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    KubernetesClient kubernetesClient() {
        return new KubernetesClientBuilder().build();
    }
}
