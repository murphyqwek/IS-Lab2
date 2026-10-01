import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { API_CONFIG } from '../core/api.config';
import {
  CoordinatesRequest,
  CoordinatesResponse,
  EventRequest,
  EventResponse,
  LocationRequest,
  LocationResponse,
  PersonRequest,
  PersonResponse,
  VenueRequest,
  VenueResponse,
} from '../models/ticket.models';

export interface ReferenceDataSnapshot {
  coordinates: CoordinatesResponse[];
  persons: PersonResponse[];
  events: EventResponse[];
  venues: VenueResponse[];
  locations: LocationResponse[];
}

@Injectable({ providedIn: 'root' })
export class ReferenceDataService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<ReferenceDataSnapshot> {
    return forkJoin({
      coordinates: this.getCoordinates(),
      persons: this.getPersons(),
      events: this.getEvents(),
      venues: this.getVenues(),
      locations: this.getLocations(),
    });
  }

  getCoordinates(): Observable<CoordinatesResponse[]> {
    return this.http.get<CoordinatesResponse[]>(API_CONFIG.coordinates);
  }
  createCoordinates(request: CoordinatesRequest): Observable<CoordinatesResponse> {
    return this.http.post<CoordinatesResponse>(API_CONFIG.coordinates, request);
  }
  updateCoordinates(id: number, request: CoordinatesRequest): Observable<CoordinatesResponse> {
    return this.http.put<CoordinatesResponse>(`${API_CONFIG.coordinates}/${id}`, request);
  }
  deleteCoordinates(id: number, replacementId?: number): Observable<void> {
    return this.deleteWithReplacement(API_CONFIG.coordinates, id, replacementId);
  }

  getPersons(): Observable<PersonResponse[]> {
    return this.http.get<PersonResponse[]>(API_CONFIG.persons);
  }
  createPerson(request: PersonRequest): Observable<PersonResponse> {
    return this.http.post<PersonResponse>(API_CONFIG.persons, request);
  }
  updatePerson(id: number, request: PersonRequest): Observable<PersonResponse> {
    return this.http.put<PersonResponse>(`${API_CONFIG.persons}/${id}`, request);
  }
  deletePerson(id: number, replacementId?: number): Observable<void> {
    return this.deleteWithReplacement(API_CONFIG.persons, id, replacementId);
  }

  getEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(API_CONFIG.events);
  }
  createEvent(request: EventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(API_CONFIG.events, request);
  }
  updateEvent(id: number, request: EventRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${API_CONFIG.events}/${id}`, request);
  }
  deleteEvent(id: number, replacementId?: number): Observable<void> {
    return this.deleteWithReplacement(API_CONFIG.events, id, replacementId);
  }

  getVenues(): Observable<VenueResponse[]> {
    return this.http.get<VenueResponse[]>(API_CONFIG.venues);
  }
  createVenue(request: VenueRequest): Observable<VenueResponse> {
    return this.http.post<VenueResponse>(API_CONFIG.venues, request);
  }
  updateVenue(id: number, request: VenueRequest): Observable<VenueResponse> {
    return this.http.put<VenueResponse>(`${API_CONFIG.venues}/${id}`, request);
  }
  deleteVenue(id: number, replacementId?: number): Observable<void> {
    return this.deleteWithReplacement(API_CONFIG.venues, id, replacementId);
  }

  getLocations(): Observable<LocationResponse[]> {
    return this.http.get<LocationResponse[]>(API_CONFIG.locations);
  }
  createLocation(request: LocationRequest): Observable<LocationResponse> {
    return this.http.post<LocationResponse>(API_CONFIG.locations, request);
  }
  updateLocation(id: number, request: LocationRequest): Observable<LocationResponse> {
    return this.http.put<LocationResponse>(`${API_CONFIG.locations}/${id}`, request);
  }
  deleteLocation(id: number, replacementId?: number): Observable<void> {
    return this.deleteWithReplacement(API_CONFIG.locations, id, replacementId);
  }

  private deleteWithReplacement(baseUrl: string, id: number, replacementId?: number): Observable<void> {
    let params = new HttpParams();
    if (replacementId != null) params = params.set('replacementId', replacementId);
    return this.http.delete<void>(`${baseUrl}/${id}`, { params });
  }
}
