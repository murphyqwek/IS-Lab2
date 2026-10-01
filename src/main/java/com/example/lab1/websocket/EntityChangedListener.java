package com.example.lab1.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EntityChangedListener {

    private final SimpMessagingTemplate messagingTemplate;

    public EntityChangedListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EntityChangedEvent event) {
        messagingTemplate.convertAndSend("/topic/entities", event);
    }
}