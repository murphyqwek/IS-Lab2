import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { API_CONFIG } from '../core/api.config';
export interface AuthUser { id: number | null; username: string; role: string; }
export interface Credentials { username: string; password: string; }
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly user = signal<AuthUser | null>(this.restore());
  login(value: Credentials) { return this.http.post<AuthUser>(API_CONFIG.login, value).pipe(tap(user => this.save(user))); }
  register(value: Credentials) { return this.http.post<AuthUser>(API_CONFIG.register, value).pipe(tap(user => this.save(user))); }
  logout() { return this.http.post<void>(API_CONFIG.logout, null).pipe(tap(() => this.clear())); }
  clear(): void { this.user.set(null); try { sessionStorage.removeItem('is-user'); } catch {} }
  private save(user: AuthUser): void { this.user.set(user); try { sessionStorage.setItem('is-user', JSON.stringify(user)); } catch {} }
  private restore(): AuthUser | null {
    try { const user = JSON.parse(sessionStorage.getItem('is-user') ?? 'null'); return user && typeof user.username === 'string' ? user : null; } catch { return null; }
  }
}
