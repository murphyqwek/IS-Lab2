export const API_CONFIG = {
  login: '/api/auth/login',
  register: '/api/auth/register',
  logout: '/api/auth/logout',
  import: '/api/import',
  history: '/api/history',

  tickets: '/api/tickets',
  coordinates: '/api/coordinates',
  persons: '/api/persons',
  events: '/api/events',
  venues: '/api/venues',
  locations: '/api/locations',

  websocketEndpoint: '/ws',
  websocketTopic: '/topic/entity-changes',
} as const;
