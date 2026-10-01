import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../core/api.config';
import {
  PageResponse,
  TicketQuery,
  TicketRequest,
  TicketResponse,
  VenueResponse,
} from '../models/ticket.models';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = API_CONFIG.tickets;

  getAll(query: TicketQuery): Observable<PageResponse<TicketResponse>> {
    let params = new HttpParams()
      .set('page', query.page)
      .set('size', query.size)
      .set('sortBy', query.sortBy)
      .set('direction', query.direction);

    if (query.name?.trim()) params = params.set('name', query.name.trim());
    if (query.eventName?.trim()) params = params.set('eventName', query.eventName.trim());
    if (query.eventDescription?.trim()) params = params.set('eventDescription', query.eventDescription.trim());
    if (query.venueName?.trim()) params = params.set('venueName', query.venueName.trim());

    return this.http.get<PageResponse<TicketResponse>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<TicketResponse> {
    return this.http.get<TicketResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: TicketRequest): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(this.baseUrl, request);
  }

  update(id: number, request: TicketRequest): Observable<TicketResponse> {
    return this.http.put<TicketResponse>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getWithMaxType(): Observable<TicketResponse> {
    return this.http.get<TicketResponse>(`${this.baseUrl}/max-type`);
  }

  countWithVenueLessThan(venueId: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/venue-less-than/${venueId}/count`);
  }

  getUniqueVenues(): Observable<VenueResponse[]> {
    return this.http.get<VenueResponse[]>(`${this.baseUrl}/unique-venues`);
  }

  copyAsVip(ticketId: number): Observable<TicketResponse> {
    return this.http.post<TicketResponse>(`${this.baseUrl}/${ticketId}/copy-vip`, null);
  }

  copyWithDiscount(ticketId: number, discount: number): Observable<TicketResponse> {
    const params = new HttpParams().set('discount', discount);
    return this.http.post<TicketResponse>(`${this.baseUrl}/${ticketId}/copy-with-discount`, null, { params });
  }
}
