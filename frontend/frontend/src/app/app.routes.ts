import { Routes } from '@angular/router';
import { TicketsPageComponent } from './components/tickets-page/tickets-page';
import { TicketDetailsComponent } from './components/ticket-details/ticket-details';
import { ReferenceDataComponent } from './components/reference-data/reference-data';
import { SpecialOperationsComponent } from './components/special-operations/special-operations';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'tickets' },
  { path: 'tickets', component: TicketsPageComponent },
  { path: 'tickets/:id', component: TicketDetailsComponent },
  { path: 'references', component: ReferenceDataComponent },
  { path: 'operations', component: SpecialOperationsComponent },
  { path: '**', redirectTo: 'tickets' },
];
