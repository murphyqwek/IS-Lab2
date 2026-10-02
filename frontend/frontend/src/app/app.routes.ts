import { inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';

import { AuthService } from './services/auth.service';
import { TicketsPageComponent } from './components/tickets-page/tickets-page';
import { TicketDetailsComponent } from './components/ticket-details/ticket-details';
import { ReferenceDataComponent } from './components/reference-data/reference-data';
import { SpecialOperationsComponent } from './components/special-operations/special-operations';
import { AuthPageComponent } from './components/auth-page/auth-page';

const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.user()
    ? true
    : router.createUrlTree(['/login'], {
        queryParams: { returnUrl: state.url },
      });
};

export const routes: Routes = [
  { path: 'login', component: AuthPageComponent },
  {
    path: 'register',
    component: AuthPageComponent,
    data: { registration: true },
  },

  { path: '', pathMatch: 'full', redirectTo: 'tickets' },
  {
    path: 'tickets',
    component: TicketsPageComponent,
    canActivate: [authGuard],
  },
  {
    path: 'tickets/:id',
    component: TicketDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'references',
    component: ReferenceDataComponent,
    canActivate: [authGuard],
  },
  {
    path: 'operations',
    component: SpecialOperationsComponent,
    canActivate: [authGuard],
  },

  { path: '**', redirectTo: 'tickets' },
];