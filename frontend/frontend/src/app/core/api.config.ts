export const API_CONFIG = {
  tickets: '/api/tickets',
  coordinates: '/api/coordinates',
  persons: '/api/persons',
  events: '/api/events',
  venues: '/api/venues',
  locations: '/api/locations',

  websocketEndpoint: '/ws',
  websocketTopic: '/topic/entity-changes',
} as const;
