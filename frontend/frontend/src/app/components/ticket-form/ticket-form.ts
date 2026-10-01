import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  Color,
  Country,
  EventType,
  ReferenceRequest,
  TicketRequest,
  TicketResponse,
  TicketType,
  VenueType,
  CoordinatesRequest,
  EventRequest,
  LocationRequest,
  PersonRequest,
  VenueRequest,
} from '../../models/ticket.models';
import { ReferenceDataService, ReferenceDataSnapshot } from '../../services/reference-data.service';
import { ApiErrorService } from '../../services/api-error.service';

export type ReferenceMode = 'existing' | 'new';
export type OptionalReferenceMode = 'none' | ReferenceMode;

@Component({
  selector: 'app-ticket-form',
  imports: [FormsModule],
  templateUrl: './ticket-form.html',
  styleUrl: './ticket-form.scss',
})
export class TicketFormComponent implements OnInit, OnChanges {
  @Input() ticket: TicketResponse | null = null;
  @Input() serverError = '';
  @Output() cancelled = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<TicketRequest>();

  readonly ticketTypes: TicketType[] = ['VIP', 'USUAL', 'BUDGETARY', 'CHEAP'];
  readonly colors: Color[] = ['RED', 'WHITE', 'BROWN'];
  readonly countries: Country[] = ['USA', 'ITALY', 'THAILAND'];
  readonly eventTypes: EventType[] = ['CONCERT', 'BASKETBALL', 'OPERA', 'EXPOSITION'];
  readonly venueTypes: VenueType[] = ['BAR', 'LOFT', 'CINEMA'];

  private readonly referencesService = inject(ReferenceDataService);
  private readonly apiError = inject(ApiErrorService);

  references: ReferenceDataSnapshot = {
    coordinates: [], persons: [], events: [], venues: [], locations: [],
  };
  loadingReferences = false;
  referenceError = '';
  formError = '';

  name = '';
  price: number | null = null;
  ticketType: TicketType | '' = '';
  discount: number | null = null;
  number: number | null = null;

  coordinatesMode: ReferenceMode = 'new';
  coordinatesId: number | null = null;
  coordinatesX: number | null = null;
  coordinatesY: number | null = null;

  eventMode: ReferenceMode = 'new';
  eventId: number | null = null;
  eventName = '';
  eventDescription = '';
  eventType: EventType | '' = '';

  personMode: OptionalReferenceMode = 'none';
  personId: number | null = null;
  eyeColor: Color | '' = '';
  hairColor: Color | '' = '';
  weight: number | null = null;
  nationality: Country | '' = '';

  locationMode: OptionalReferenceMode = 'none';
  locationId: number | null = null;
  locationX: number | null = null;
  locationY: number | null = null;
  locationZ: number | null = null;
  locationName = '';

  venueMode: OptionalReferenceMode = 'none';
  venueId: number | null = null;
  venueName = '';
  venueCapacity: number | null = null;
  venueType: VenueType | '' = '';

  ngOnInit(): void {
    this.loadReferences();
    this.fillFromTicket();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ticket']) this.fillFromTicket();
  }

  submit(): void {
    this.formError = '';
    const error = this.validate();
    if (error) {
      this.formError = error;
      return;
    }

    this.submitted.emit({
      name: this.name.trim(),
      coordinates: this.coordinatesReference(),
      person: this.personReference(),
      event: this.eventReference(),
      price: Number(this.price),
      ticketType: this.ticketType || null,
      discount: Number(this.discount),
      number: this.number == null ? null : Number(this.number),
      venue: this.venueReference(),
    });
  }

  private loadReferences(): void {
    this.loadingReferences = true;
    this.referenceError = '';
    this.referencesService.getAll().subscribe({
      next: data => {
        this.references = data;
        this.loadingReferences = false;
      },
      error: error => {
        this.referenceError = this.apiError.message(error);
        this.loadingReferences = false;
      },
    });
  }

  private fillFromTicket(): void {
    const ticket = this.ticket;
    if (!ticket) return;

    this.name = ticket.name;
    this.price = ticket.price;
    this.ticketType = ticket.ticketType ?? '';
    this.discount = ticket.discount;
    this.number = ticket.number;

    this.coordinatesMode = 'existing';
    this.coordinatesId = ticket.coordinates.id;

    this.eventMode = 'existing';
    this.eventId = ticket.event.id;

    if (ticket.person) {
      this.personMode = 'existing';
      this.personId = ticket.person.id;
    } else {
      this.personMode = 'none';
      this.personId = null;
    }

    if (ticket.venue) {
      this.venueMode = 'existing';
      this.venueId = ticket.venue.id;
    } else {
      this.venueMode = 'none';
      this.venueId = null;
    }
  }

  private validate(): string {
    if (!this.name.trim()) return 'Название билета обязательно.';
    if (this.price == null || this.price <= 0) return 'Цена должна быть больше 0.';
    if (this.discount == null || this.discount < 1 || this.discount > 100) return 'Скидка должна быть от 1 до 100.';
    if (this.number != null && this.number <= 0) return 'Number, если указан, должен быть больше 0.';

    if (this.coordinatesMode === 'existing' && this.coordinatesId == null) return 'Выбери существующие Coordinates.';
    if (this.coordinatesMode === 'new' && (this.coordinatesX == null || this.coordinatesY == null)) return 'Для новых Coordinates заполни X и Y.';

    if (this.eventMode === 'existing' && this.eventId == null) return 'Выбери существующий Event.';
    if (this.eventMode === 'new' && (!this.eventName.trim() || this.eventDescription == null)) return 'Для нового Event заполни name и description.';

    if (this.personMode === 'existing' && this.personId == null) return 'Выбери существующий Person.';
    if (this.personMode === 'new') {
      if (this.weight != null && this.weight <= 0) return 'Вес Person должен быть больше 0.';
      if (this.locationMode === 'existing' && this.locationId == null) return 'Выбери существующий Location.';
      if (this.locationMode === 'new') {
        if (this.locationX == null || this.locationY == null || this.locationZ == null) return 'Для нового Location заполни X, Y и Z.';
        if (this.locationName.length > 692) return 'Location.name не может быть длиннее 692 символов.';
        if (!Number.isInteger(Number(this.locationY)) || !Number.isInteger(Number(this.locationZ))) return 'Location Y и Z должны быть целыми числами.';
      }
    }

    if (this.venueMode === 'existing' && this.venueId == null) return 'Выбери существующий Venue.';
    if (this.venueMode === 'new') {
      if (!this.venueName.trim()) return 'Название Venue обязательно.';
      if (this.venueCapacity == null || this.venueCapacity <= 0) return 'Вместимость Venue должна быть больше 0.';
    }

    return '';
  }

  private coordinatesReference(): ReferenceRequest<CoordinatesRequest> {
    return this.coordinatesMode === 'existing'
      ? { id: Number(this.coordinatesId), newObject: null }
      : { id: null, newObject: { x: Number(this.coordinatesX), y: Number(this.coordinatesY) } };
  }

  private eventReference(): ReferenceRequest<EventRequest> {
    return this.eventMode === 'existing'
      ? { id: Number(this.eventId), newObject: null }
      : {
          id: null,
          newObject: {
            name: this.eventName.trim(),
            description: this.eventDescription,
            eventType: this.eventType || null,
          },
        };
  }

  private personReference(): ReferenceRequest<PersonRequest> | null {
    if (this.personMode === 'none') return null;
    if (this.personMode === 'existing') return { id: Number(this.personId), newObject: null };

    return {
      id: null,
      newObject: {
        eyeColor: this.eyeColor || null,
        hairColor: this.hairColor || null,
        location: this.locationReference(),
        weight: this.weight == null ? null : Number(this.weight),
        nationality: this.nationality || null,
      },
    };
  }

  private locationReference(): ReferenceRequest<LocationRequest> | null {
    if (this.locationMode === 'none') return null;
    if (this.locationMode === 'existing') return { id: Number(this.locationId), newObject: null };

    return {
      id: null,
      newObject: {
        x: Number(this.locationX),
        y: Number(this.locationY),
        z: Number(this.locationZ),
        name: this.locationName.trim() || null,
      },
    };
  }

  private venueReference(): ReferenceRequest<VenueRequest> | null {
    if (this.venueMode === 'none') return null;
    if (this.venueMode === 'existing') return { id: Number(this.venueId), newObject: null };

    return {
      id: null,
      newObject: {
        name: this.venueName.trim(),
        capacity: Number(this.venueCapacity),
        venueType: this.venueType || null,
      },
    };
  }
}
