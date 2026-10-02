import { Component, ElementRef, HostListener, OnDestroy, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { finalize } from 'rxjs';
import { RealtimeService } from '../../services/realtime.service';
import { AuthService } from '../../services/auth.service';
import { ImportService, validateImportFile } from '../../services/import.service';
import { ApiErrorService } from '../../services/api-error.service';
@Component({ selector: 'app-header', imports: [RouterLink, RouterLinkActive], templateUrl: './app-header.html', styleUrl: './app-header.scss' })
export class AppHeaderComponent implements OnDestroy {
  constructor() { this.realtime.connect(); }
  ngOnDestroy(): void { this.realtime.disconnect(); }
  readonly realtime = inject(RealtimeService);
  readonly auth = inject(AuthService);
  readonly imports = inject(ImportService);
  private readonly errors = inject(ApiErrorService);
  private readonly router = inject(Router);
  private readonly element = inject(ElementRef<HTMLElement>);
  readonly menuOpen = signal(false);
  readonly uploading = signal(false);
  readonly loggingOut = signal(false);
  @HostListener('document:click', ['$event']) outside(event: MouseEvent) {
    if (!this.element.nativeElement.querySelector('.user-menu')?.contains(event.target as Node)) this.menuOpen.set(false);
  }
  @HostListener('document:keydown.escape') escape() { this.menuOpen.set(false); }
  logout(): void {
    if (this.loggingOut()) return;
    this.loggingOut.set(true);
    this.auth.logout().pipe(finalize(() => this.loggingOut.set(false))).subscribe({
      next: () => { this.menuOpen.set(false); this.imports.historyOpen.set(false); void this.router.navigate(['/login']); },
      error: error => this.imports.notice.set({ text: this.errors.message(error), error: true }),
    });
  }
  async chooseFile(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file || this.uploading()) return;
    const error = validateImportFile(file);
    if (error) { this.imports.notice.set({ text: error, error: true }); return; }
    this.uploading.set(true);
    try {
      if (!(await file.text()).trim()) throw new Error('Файл не содержит данных: только пробельные символы');
    } catch (error) {
      this.uploading.set(false);
      this.imports.notice.set({ text: this.errors.message(error), error: true });
      return;
    }
    this.imports.upload(file).pipe(finalize(() => { this.uploading.set(false); this.imports.revision.update(v => v + 1); })).subscribe({
      next: () => { this.imports.notice.set({ text: 'Файл отправлен на импорт. Результат доступен в истории', error: false }); this.imports.historyOpen.set(true); },
      error: error => this.imports.notice.set({ text: this.errors.message(error), error: true }),
    });
  }
}
