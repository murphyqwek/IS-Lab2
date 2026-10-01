import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TicketResponse, VenueResponse } from '../../models/ticket.models';
import { TicketService } from '../../services/ticket.service';
import { ApiErrorService } from '../../services/api-error.service';

@Component({
  selector: 'app-special-operations',
  imports: [FormsModule],
  templateUrl: './special-operations.html',
  styleUrl: './special-operations.scss',
})
export class SpecialOperationsComponent {
  private readonly tickets = inject(TicketService);
  private readonly apiError = inject(ApiErrorService);

  readonly error = signal('');
  readonly message = signal('');
  readonly ticketResult = signal<TicketResponse | null>(null);
  readonly venuesResult = signal<VenueResponse[]>([]);
  readonly countResult = signal<number | null>(null);

  venueId: number | null = null;
  vipTicketId: number | null = null;
  discountTicketId: number | null = null;
  discount: number | null = null;

  maxType(): void {
    this.start();
    this.tickets.getWithMaxType().subscribe({
      next: ticket => {
        this.ticketResult.set(ticket);
        this.message.set('Получен билет с максимальным значением type.');
      },
      error: error => this.fail(error),
    });
  }

  countVenueLess(): void {
    if (this.venueId == null || this.venueId <= 0) {
      this.error.set('Укажи положительный venueId.');
      return;
    }
    this.start();
    this.tickets.countWithVenueLessThan(this.venueId).subscribe({
      next: count => {
        this.countResult.set(count);
        this.message.set('Количество рассчитано функцией БД.');
      },
      error: error => this.fail(error),
    });
  }

  uniqueVenues(): void {
    this.start();
    this.tickets.getUniqueVenues().subscribe({
      next: venues => {
        this.venuesResult.set(venues);
        this.message.set(`Получено уникальных Venue: ${venues.length}.`);
      },
      error: error => this.fail(error),
    });
  }

  copyVip(): void {
    if (this.vipTicketId == null || this.vipTicketId <= 0) {
      this.error.set('Укажи положительный ID билета.');
      return;
    }
    this.start();
    this.tickets.copyAsVip(this.vipTicketId).subscribe({
      next: ticket => {
        this.ticketResult.set(ticket);
        this.message.set(`Создан VIP-билет #${ticket.id} с удвоенной ценой.`);
      },
      error: error => this.fail(error),
    });
  }

  copyDiscount(): void {
    if (this.discountTicketId == null || this.discountTicketId <= 0) {
      this.error.set('Укажи положительный ID билета.');
      return;
    }
    if (this.discount == null || this.discount < 1 || this.discount > 100) {
      this.error.set('Скидка должна быть от 1 до 100%.');
      return;
    }
    this.start();
    this.tickets.copyWithDiscount(this.discountTicketId, this.discount).subscribe({
      next: ticket => {
        this.ticketResult.set(ticket);
        this.message.set(`Создан новый билет #${ticket.id}.`);
      },
      error: error => this.fail(error),
    });
  }

  private start(): void {
    this.error.set('');
    this.message.set('');
    this.ticketResult.set(null);
    this.venuesResult.set([]);
    this.countResult.set(null);
  }

  private fail(error: unknown): void {
    this.error.set(this.apiError.message(error));
  }
}
