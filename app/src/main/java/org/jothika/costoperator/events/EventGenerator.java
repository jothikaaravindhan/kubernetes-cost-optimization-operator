package org.jothika.costoperator.events;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventBuilder;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.ObjectReferenceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class EventGenerator {

    private final KubernetesClient kubernetesClient;

    public EventGenerator(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public void generateEvent(
            HasMetadata resource, String eventName, String message, EventType type)
            throws KubernetesClientException {

        ObjectReference objectReference =
                new ObjectReferenceBuilder()
                        .withApiVersion(resource.getApiVersion())
                        .withKind(resource.getKind())
                        .withName(resource.getMetadata().getName())
                        .withNamespace(resource.getMetadata().getNamespace())
                        .withUid(resource.getMetadata().getUid())
                        .build();

        Event event =
                new EventBuilder()
                        .withNewMetadata()
                        .withName(eventName)
                        .withNamespace(resource.getMetadata().getNamespace())
                        .endMetadata()
                        .withInvolvedObject(objectReference)
                        .withType(type.getType())
                        .withMessage(message)
                        .withFirstTimestamp(
                                DateTimeFormatter.ISO_INSTANT.format(
                                        ZonedDateTime.now(ZoneOffset.UTC)))
                        .withLastTimestamp(
                                DateTimeFormatter.ISO_INSTANT.format(
                                        ZonedDateTime.now(ZoneOffset.UTC)))
                        .withNewSource()
                        .withComponent("cost-optimization-operator")
                        .endSource()
                        .build();
        kubernetesClient
                .v1()
                .events()
                .inNamespace(resource.getMetadata().getNamespace())
                .resource(event)
                .create();
    }
}
