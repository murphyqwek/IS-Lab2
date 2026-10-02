import { provideZonelessChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { RealtimeService } from './services/realtime.service';
import { signal } from '@angular/core';
describe('App', () => {
  it('renders the application navigation', async () => {
    await TestBed.configureTestingModule({ imports: [App], providers: [
      provideZonelessChangeDetection(), provideHttpClient(), provideRouter([]),
      { provide: RealtimeService, useValue: { connected: signal(false), connect() {}, disconnect() {} } },
    ] }).compileComponents();
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('nav').textContent).toContain('Билеты');
  });
});
