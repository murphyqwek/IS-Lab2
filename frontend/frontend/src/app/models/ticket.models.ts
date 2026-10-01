export type TicketType = 'VIP' | 'USUAL' | 'BUDGETARY' | 'CHEAP';
export type Color = 'RED' | 'WHITE' | 'BROWN';
export type Country = 'USA' | 'ITALY' | 'THAILAND';
export type EventType = 'CONCERT' | 'BASKETBALL' | 'OPERA' | 'EXPOSITION';
export type VenueType = 'BAR' | 'LOFT' | 'CINEMA';
export type SortDirection = 'ASC' | 'DESC';

// Названия соответствуют предполагаемому TicketSortField на backend.
// Если enum в Java назван иначе, поправь только этот union и options в tickets-page.ts.
export type TicketSortField = 'NAME' | 'EVENT_NAME' | 'EVENT_DESCRIPTION' | 'VENUE_NAME';

export interface CoordinatesResponse {
  id: number;
  x: number;
  y: number;
}

export interface LocationResponse {
  id: number;
  x: number;
  y: number;
  z: number;
  name: string | null;
}

export interface PersonResponse {
  id: number;
  eyeColor: Color | null;
  hairColor: Color | null;
  location: LocationResponse | null;
  weight: number | null;
  nationality: Country | null;
}

export interface EventResponse {
  id: number;
  name: string;
  description: string;
  eventType: EventType | null;
}

export interface VenueResponse {
  id: number;
  name: string;
  capacity: number;
  venueType: VenueType | null;
}

export interface TicketResponse {
  id: number;
  name: string;
  coordinates: CoordinatesResponse;
  creationDate: string;
  person: PersonResponse | null;
  event: EventResponse;
  price: number;
  ticketType: TicketType | null;
  discount: number;
  number: number | null;
  venue: VenueResponse | null;
}

export interface CoordinatesRequest {
  x: number;
  y: number;
}

export interface LocationRequest {
  x: number;
  y: number;
  z: number;
  name: string | null;
}

export interface EventRequest {
  name: string;
  description: string;
  eventType: EventType | null;
}

export interface VenueRequest {
  name: string;
  capacity: number;
  venueType: VenueType | null;
}

export interface ReferenceRequest<T> {
  id: number | null;
  newObject: T | null;
}

export interface PersonRequest {
  eyeColor: Color | null;
  hairColor: Color | null;
  location: ReferenceRequest<LocationRequest> | null;
  weight: number | null;
  nationality: Country | null;
}

export interface TicketRequest {
  name: string;
  coordinates: ReferenceRequest<CoordinatesRequest>;
  person: ReferenceRequest<PersonRequest> | null;
  event: ReferenceRequest<EventRequest>;
  price: number;
  ticketType: TicketType | null;
  discount: number;
  number: number | null;
  venue: ReferenceRequest<VenueRequest> | null;
}

export interface TicketFilter {
  name?: string;
  eventName?: string;
  eventDescription?: string;
  venueName?: string;
}

export interface TicketQuery extends TicketFilter {
  page: number;
  size: number;
  sortBy: TicketSortField;
  direction: SortDirection;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements?: number;
  empty?: boolean;
}

export type EntityType = 'TICKET' | 'COORDINATES' | 'PERSON' | 'EVENT' | 'VENUE' | 'LOCATION' | string;
export type ChangeType = 'CREATED' | 'UPDATED' | 'DELETED' | string;

export interface EntityChangedEvent {
  entityType: EntityType;
  changeType: ChangeType;
  id: number;
}
