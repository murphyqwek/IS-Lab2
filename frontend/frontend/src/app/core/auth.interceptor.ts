import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ImportService } from '../services/import.service';
import { AuthService } from '../services/auth.service';
import { API_CONFIG } from './api.config';
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const imports = inject(ImportService);
  if (!request.url.startsWith('/api/')) return next(request);
  return next(request.clone({ withCredentials: true })).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 403 || error.status === 401) {
      auth.clear();
      imports.historyOpen.set(false);

      if (request.url !== API_CONFIG.login && !router.url.startsWith('/login')) {
        const returnUrl = router.url.startsWith('/register') ? '/tickets' : router.url;
        void router.navigate(['/login'], { queryParams: { returnUrl }, replaceUrl: true });
      }
    }
    return throwError(() => error);
  }));
};
