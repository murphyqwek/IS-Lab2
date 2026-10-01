import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import {
  Color,
  CoordinatesResponse,
  Country,
  EventResponse,
  EventType,
  LocationResponse,
  PersonRequest,
  PersonResponse,
  VenueResponse,
  VenueType,
} from '../../models/ticket.models';
import { ApiErrorService } from '../../services/api-error.service';
import { ReferenceDataService, ReferenceDataSnapshot } from '../../services/reference-data.service';
import { RealtimeService } from '../../services/realtime.service';

type RefType = 'coordinates' | 'events' | 'venues' | 'locations' | 'persons';
type RefEntity = CoordinatesResponse | EventResponse | VenueResponse | LocationResponse | PersonResponse;
type LocationMode = 'none' | 'existing' | 'new';

@Component({
  selector: 'app-reference-data',
  imports: [FormsModule],
  templateUrl: './reference-data.html',
  styleUrl: './reference-data.scss',
})
export class ReferenceDataComponent implements OnInit, OnDestroy {
  private readonly service = inject(ReferenceDataService);
  private readonly apiError = inject(ApiErrorService);
  private readonly realtime = inject(RealtimeService);

  readonly data = signal<ReferenceDataSnapshot>({ coordinates: [], persons: [], events: [], venues: [], locations: [] });
  readonly loading = signal(false);
  readonly error = signal('');

  readonly colors: Color[] = ['RED', 'WHITE', 'BROWN'];
  readonly countries: Country[] = ['USA', 'ITALY', 'THAILAND'];
  readonly eventTypes: EventType[] = ['CONCERT', 'BASKETBALL', 'OPERA', 'EXPOSITION'];
  readonly venueTypes: VenueType[] = ['BAR', 'LOFT', 'CINEMA'];

  tab: RefType = 'coordinates';
  editorType: RefType | null = null;
  editingId: number | null = null;
  formError = '';

  x: number | null = null;
  y: number | null = null;
  z: number | null = null;
  name = '';
  description = '';
  capacity: number | null = null;
  eventType: EventType | '' = '';
  venueType: VenueType | '' = '';

  eyeColor: Color | '' = '';
  hairColor: Color | '' = '';
  weight: number | null = null;
  nationality: Country | '' = '';
  personLocationMode: LocationMode = 'none';
  personLocationId: number | null = null;
  locationX: number | null = null;
  locationY: number | null = null;
  locationZ: number | null = null;
  locationName = '';

  deleteType: RefType | null = null;
  deleteEntity: RefEntity | null = null;
  replacementId: number | null = null;

  private unsubscribeRealtime?: () => void;

  ngOnInit(): void {
    this.load();
    this.unsubscribeRealtime = this.realtime.subscribe(() => this.load(false));
  }

  ngOnDestroy(): void {
    this.unsubscribeRealtime?.();
  }

  setTab(tab: RefType): void {
    this.tab = tab;
  }

  openCreate(type: RefType): void {
    this.resetEditor();
    this.editorType = type;
  }

  openEdit(type: RefType, entity: RefEntity): void {
    this.resetEditor();
    this.editorType = type;
    this.editingId = entity.id;

    if (type === 'coordinates') {
      const item = entity as CoordinatesResponse;
      this.x = item.x; this.y = item.y;
    } else if (type === 'events') {
      const item = entity as EventResponse;
      this.name = item.name; this.description = item.description; this.eventType = item.eventType ?? '';
    } else if (type === 'venues') {
      const item = entity as VenueResponse;
      this.name = item.name; this.capacity = item.capacity; this.venueType = item.venueType ?? '';
    } else if (type === 'locations') {
      const item = entity as LocationResponse;
      this.x = item.x; this.y = item.y; this.z = item.z; this.name = item.name ?? '';
    } else {
      const item = entity as PersonResponse;
      this.eyeColor = item.eyeColor ?? '';
      this.hairColor = item.hairColor ?? '';
      this.weight = item.weight;
      this.nationality = item.nationality ?? '';
      if (item.location) {
        this.personLocationMode = 'existing';
        this.personLocationId = item.location.id;
      }
    }
  }

  closeEditor(): void {
    this.editorType = null;
    this.formError = '';
  }

  save(): void {
    if (!this.editorType) return;
    this.formError = this.validateEditor();
    if (this.formError) return;

    let operation: Observable<unknown>;
    const id = this.editingId;

    switch (this.editorType) {
      case 'coordinates': {
        const request = { x: Number(this.x), y: Number(this.y) };
        operation = id == null ? this.service.createCoordinates(request) : this.service.updateCoordinates(id, request);
        break;
      }
      case 'events': {
        const request = { name: this.name.trim(), description: this.description, eventType: this.eventType || null };
        operation = id == null ? this.service.createEvent(request) : this.service.updateEvent(id, request);
        break;
      }
      case 'venues': {
        const request = { name: this.name.trim(), capacity: Number(this.capacity), venueType: this.venueType || null };
        operation = id == null ? this.service.createVenue(request) : this.service.updateVenue(id, request);
        break;
      }
      case 'locations': {
        const request = { x: Number(this.x), y: Number(this.y), z: Number(this.z), name: this.name.trim() || null };
        operation = id == null ? this.service.createLocation(request) : this.service.updateLocation(id, request);
        break;
      }
      case 'persons': {
        const request: PersonRequest = {
          eyeColor: this.eyeColor || null,
          hairColor: this.hairColor || null,
          location: this.personLocationReference(),
          weight: this.weight == null ? null : Number(this.weight),
          nationality: this.nationality || null,
        };
        operation = id == null ? this.service.createPerson(request) : this.service.updatePerson(id, request);
        break;
      }
    }

    operation.subscribe({
      next: () => {
        this.closeEditor();
        this.load(false);
      },
      error: (error: unknown) => this.formError = this.apiError.message(error),
    });
  }

  openDelete(type: RefType, entity: RefEntity): void {
    this.deleteType = type;
    this.deleteEntity = entity;
    this.replacementId = null;
  }

  closeDelete(): void {
    this.deleteType = null;
    this.deleteEntity = null;
    this.replacementId = null;
  }

  confirmDelete(): void {
    if (!this.deleteType || !this.deleteEntity) return;
    const id = this.deleteEntity.id;
    const replacement = this.replacementId ?? undefined;

    let operation: Observable<void>;
    switch (this.deleteType) {
      case 'coordinates': operation = this.service.deleteCoordinates(id, replacement); break;
      case 'events': operation = this.service.deleteEvent(id, replacement); break;
      case 'venues': operation = this.service.deleteVenue(id, replacement); break;
      case 'locations': operation = this.service.deleteLocation(id, replacement); break;
      case 'persons': operation = this.service.deletePerson(id, replacement); break;
    }

    operation.subscribe({
      next: () => {
        this.closeDelete();
        this.load(false);
      },
      error: (error: unknown) => this.error.set(this.apiError.message(error)),
    });
  }

  replacementCandidates(): RefEntity[] {
    if (!this.deleteType || !this.deleteEntity) return [];
    const id = this.deleteEntity.id;
    switch (this.deleteType) {
      case 'coordinates': return this.data().coordinates.filter(x => x.id !== id);
      case 'events': return this.data().events.filter(x => x.id !== id);
      case 'venues': return this.data().venues.filter(x => x.id !== id);
      case 'locations': return this.data().locations.filter(x => x.id !== id);
      case 'persons': return this.data().persons.filter(x => x.id !== id);
    }
  }

  entityLabel(entity: RefEntity): string {
    if ('description' in entity) return `#${entity.id} — ${entity.name}`;
    if ('capacity' in entity) return `#${entity.id} — ${entity.name}`;
    if ('z' in entity) return `#${entity.id} — ${entity.name || 'Location'} (${entity.x}, ${entity.y}, ${entity.z})`;
    if ('eyeColor' in entity) return `#${entity.id} — ${entity.nationality || 'Person'}`;
    return `#${entity.id} — (${entity.x}, ${entity.y})`;
  }

  private load(showLoader = true): void {
    if (showLoader) this.loading.set(true);
    this.service.getAll().subscribe({
      next: data => {
        this.data.set(data);
        this.loading.set(false);
        this.error.set('');
      },
      error: error => {
        this.error.set(this.apiError.message(error));
        this.loading.set(false);
      },
    });
  }

  private resetEditor(): void {
    this.editingId = null;
    this.formError = '';
    this.x = null; this.y = null; this.z = null; this.name = ''; this.description = ''; this.capacity = null;
    this.eventType = ''; this.venueType = '';
    this.eyeColor = ''; this.hairColor = ''; this.weight = null; this.nationality = '';
    this.personLocationMode = 'none'; this.personLocationId = null;
    this.locationX = null; this.locationY = null; this.locationZ = null; this.locationName = '';
  }

  private validateEditor(): string {
    switch (this.editorType) {
      case 'coordinates':
        return this.x == null || this.y == null ? 'X и Y обязательны.' : '';
      case 'events':
        return !this.name.trim() ? 'Event.name обязателен.' : '';
      case 'venues':
        if (!this.name.trim()) return 'Venue.name обязателен.';
        if (this.capacity == null || this.capacity <= 0) return 'Capacity должен быть больше 0.';
        return '';
      case 'locations':
        if (this.x == null || this.y == null || this.z == null) return 'Location X, Y, Z обязательны.';
        if (!Number.isInteger(Number(this.y)) || !Number.isInteger(Number(this.z))) return 'Location Y и Z должны быть целыми.';
        if (this.name.length > 692) return 'Location.name максимум 692 символа.';
        return '';
      case 'persons':
        if (this.weight != null && this.weight <= 0) return 'Weight должен быть больше 0.';
        if (this.personLocationMode === 'existing' && this.personLocationId == null) return 'Выбери Location.';
        if (this.personLocationMode === 'new') {
          if (this.locationX == null || this.locationY == null || this.locationZ == null) return 'Для нового Location заполни X, Y, Z.';
          if (!Number.isInteger(Number(this.locationY)) || !Number.isInteger(Number(this.locationZ))) return 'Location Y и Z должны быть целыми.';
          if (this.locationName.length > 692) return 'Location.name максимум 692 символа.';
        }
        return '';
      default:
        return '';
    }
  }

  private personLocationReference() {
    if (this.personLocationMode === 'none') return null;
    if (this.personLocationMode === 'existing') return { id: Number(this.personLocationId), newObject: null };
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
}
