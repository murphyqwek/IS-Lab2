package com.example.lab1.websocket;

public record EntityChangedEvent(EntityType entityType, ChangeType changeType, Long entityId) {
}