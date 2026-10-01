import { DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TicketRequest, TicketResponse } from '../../models/ticket.models';
import { TicketService } from '../../services/ticket.service';
import { ApiErrorService } from '../../services/api-error.service';
import { RealtimeService } from '../../services/realtime.service';
import { TicketFormComponent } from '../ticket-form/ticket-form';

@Component({
  selector: 'app-ticket-details',
  imports: [RouterLink, DatePipe, TicketFormComponent],
  templateUrl: './ticket-details.html',
  styleUrl: './ticket-details.scss',
})
export class TicketDetailsComponent implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly ticketsService = inject(TicketService);
  private readonly apiError = inject(ApiErrorService);
  private readonly realtime = inject(RealtimeService);

  readonly ticket = signal<TicketResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  showEdit = false;
  private id = 0;
  private unsubscribeRealtime?: () => void;

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(this.id) || this.id <= 0) {
      this.error.set('Некорректный ID билета.');
      this.loading.set(false);
      return;
    }

    this.load();
    this.unsubscribeRealtime = this.realtime.subscribe(event => {
      if (event.entityType === 'TICKET' && event.id === this.id && event.changeType === 'DELETED') {
        void this.router.navigate(['/tickets']);
        return;
      }
      this.load(false);
    });
  }

  ngOnDestroy(): void {
    this.unsubscribeRealtime?.();
  }

  update(request: TicketRequest): void {
    this.ticketsService.update(this.id, request).subscribe({
      next: ticket => {
        this.ticket.set(ticket);
        this.showEdit = false;
      },
      error: error => this.error.set(this.apiError.message(error)),
    });
  }

  delete(): void {
    const ticket = this.ticket();
    if (!ticket || !confirm(`Удалить билет #${ticket.id} «${ticket.name}»?`)) return;

    this.ticketsService.delete(ticket.id).subscribe({
      next: () => void this.router.navigate(['/tickets']),
      error: error => this.error.set(this.apiError.message(error)),
    });
  }

  private load(showLoader = true): void {
    if (showLoader) this.loading.set(true);
    this.ticketsService.getById(this.id).subscribe({
      next: ticket => {
        this.ticket.set(ticket);
        this.loading.set(false);
        this.error.set('');
      },
      error: error => {
        this.error.set(this.apiError.message(error));
        this.loading.set(false);
      },
    });
  }
}
