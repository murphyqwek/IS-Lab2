package com.example.is.websocket;

public record EntityChangedEvent(EntityType entityType, ChangeType changeType, Long entityId) {
}