import { DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  PageResponse,
  SortDirection,
  TicketQuery,
  TicketRequest,
  TicketResponse,
  TicketSortField,
} from '../../models/ticket.models';
import { TicketService } from '../../services/ticket.service';
import { ApiErrorService } from '../../services/api-error.service';
import { RealtimeService } from '../../services/realtime.service';
import { TicketFormComponent } from '../ticket-form/ticket-form';

@Component({
  selector: 'app-tickets-page',
  imports: [FormsModule, RouterLink, DatePipe, TicketFormComponent],
  templateUrl: './tickets-page.html',
  styleUrl: './tickets-page.scss',
})
export class TicketsPageComponent implements OnInit, OnDestroy {
  private readonly ticketsService = inject(TicketService);
  private readonly apiError = inject(ApiErrorService);
  private readonly realtime = inject(RealtimeService);

  readonly page = signal<PageResponse<TicketResponse> | null>(null);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly saving = signal(false);

  readonly sortOptions: { value: TicketSortField; label: string }[] = [
    { value: 'NAME', label: 'Ticket.name' },
    { value: 'EVENT_NAME', label: 'Event.name' },
    { value: 'EVENT_DESCRIPTION', label: 'Event.description' },
    { value: 'VENUE_NAME', label: 'Venue.name' },
  ];

  name = '';
  eventName = '';
  eventDescription = '';
  venueName = '';
  sortBy: TicketSortField = 'NAME';
  direction: SortDirection = 'ASC';
  pageSize = 10;

  showForm = false;
  editingTicket: TicketResponse | null = null;
  private unsubscribeRealtime?: () => void;

  ngOnInit(): void {
    this.load(0);
    this.unsubscribeRealtime = this.realtime.subscribe(() => this.load(this.page()?.number ?? 0, false));
  }

  ngOnDestroy(): void {
    this.unsubscribeRealtime?.();
  }

  applyFilters(): void {
    this.load(0);
  }

  resetFilters(): void {
    this.name = '';
    this.eventName = '';
    this.eventDescription = '';
    this.venueName = '';
    this.sortBy = 'NAME';
    this.direction = 'ASC';
    this.load(0);
  }

  previousPage(): void {
    const current = this.page();
    if (current && !current.first) this.load(current.number - 1);
  }

  nextPage(): void {
    const current = this.page();
    if (current && !current.last) this.load(current.number + 1);
  }

  openCreate(): void {
    this.error.set('');
    this.editingTicket = null;
    this.showForm = true;
  }

  openEdit(ticket: TicketResponse): void {
    this.error.set('');
    this.editingTicket = ticket;
    this.showForm = true;
  }

  closeForm(): void {
    if (!this.saving()) this.showForm = false;
  }

  save(request: TicketRequest): void {
    if (this.saving()) return;
    this.saving.set(true);
    this.error.set('');

    const operation = this.editingTicket
      ? this.ticketsService.update(this.editingTicket.id, request)
      : this.ticketsService.create(request);

    operation.subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm = false;
        this.load(this.editingTicket ? (this.page()?.number ?? 0) : 0);
      },
      error: error => {
        this.saving.set(false);
        this.error.set(this.apiError.message(error));
      },
    });
  }

  delete(ticket: TicketResponse): void {
    if (!confirm(`Удалить билет #${ticket.id} «${ticket.name}»?`)) return;

    this.ticketsService.delete(ticket.id).subscribe({
      next: () => this.load(this.page()?.number ?? 0),
      error: error => this.error.set(this.apiError.message(error)),
    });
  }

  private load(pageNumber: number, showLoader = true): void {
    if (showLoader) this.loading.set(true);
    this.error.set('');

    const query: TicketQuery = {
      page: Math.max(0, pageNumber),
      size: this.pageSize,
      sortBy: this.sortBy,
      direction: this.direction,
      name: this.name,
      eventName: this.eventName,
      eventDescription: this.eventDescription,
      venueName: this.venueName,
    };

    this.ticketsService.getAll(query).subscribe({
      next: page => {
        if (page.totalPages > 0 && page.number >= page.totalPages) {
          this.load(page.totalPages - 1, false);
          return;
        }
        this.page.set(page);
        this.loading.set(false);
      },
      error: error => {
        this.error.set(this.apiError.message(error));
        this.loading.set(false);
      },
    });
  }
}
