package com.example.lab1.websocket;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EntityChangePublisher {

    private final ApplicationEventPublisher eventPublisher;

    public EntityChangePublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(EntityType entityType, ChangeType changeType, long entityId) {
        eventPublisher.publishEvent(new EntityChangedEvent(entityType, changeType, entityId));
    }
}