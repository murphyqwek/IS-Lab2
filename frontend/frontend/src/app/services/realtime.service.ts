import { Injectable, signal } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { API_CONFIG } from '../core/api.config';
import { EntityChangedEvent } from '../models/ticket.models';

@Injectable({ providedIn: 'root' })
export class RealtimeService {
  readonly connected = signal(false);
  readonly lastChange = signal<EntityChangedEvent | null>(null);

  private readonly client: Client;
  private listeners = new Set<(event: EntityChangedEvent) => void>();

  constructor() {
    this.client = new Client({
      brokerURL: this.websocketUrl(),
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: () => {},
    });

    this.client.onConnect = () => {
      this.connected.set(true);
      this.client.subscribe(API_CONFIG.websocketTopic, (message: IMessage) => this.handleMessage(message));
    };

    this.client.onDisconnect = () => this.connected.set(false);
    this.client.onWebSocketClose = () => this.connected.set(false);
    this.client.onStompError = () => this.connected.set(false);

    this.client.activate();
  }

  subscribe(listener: (event: EntityChangedEvent) => void): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  private handleMessage(message: IMessage): void {
    try {
      const event = JSON.parse(message.body) as EntityChangedEvent;
      this.lastChange.set(event);
      this.listeners.forEach(listener => listener(event));
    } catch {

    }
  }

  private websocketUrl(): string {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${protocol}//${window.location.host}${API_CONFIG.websocketEndpoint}`;
  }
}
