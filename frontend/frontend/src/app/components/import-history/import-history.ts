import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, effect, inject, signal, untracked } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { ImportService, ImportHistory, HistoryCursor } from '../../services/import.service';
import { ApiErrorService } from '../../services/api-error.service';
@Component({ selector: 'app-import-history', imports: [DatePipe], templateUrl: './import-history.html', styleUrl: './import-history.scss' })
export class ImportHistoryComponent implements AfterViewInit, OnDestroy {

  readonly imports = inject(ImportService);
  private readonly errors = inject(ApiErrorService);
  readonly rows = signal<ImportHistory[]>([]);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly hasNext = signal(true);
  private cursor: HistoryCursor | null = null;
  private request?: Subscription;
  private observer?: IntersectionObserver;
  private timer?: ReturnType<typeof setTimeout>;

  @ViewChild('scrollArea') scrollArea!: ElementRef<HTMLElement>;
  @ViewChild('sentinel') sentinel!: ElementRef<HTMLElement>;

  constructor() { effect(() => { this.imports.revision(); untracked(() => this.reset()); }); }

  ngAfterViewInit(): void {
    this.observer = new IntersectionObserver(entries => { if (entries.some(e => e.isIntersecting)) this.load(); }, { root: this.scrollArea.nativeElement, rootMargin: '160px' });
    this.observer.observe(this.sentinel.nativeElement);
  }
  reset(): void {
    this.request?.unsubscribe();

    clearTimeout(this.timer);

    this.cursor = null; this.rows.set([]); this.hasNext.set(true); this.loading.set(false); this.error.set('');

    if (this.scrollArea) 
      this.scrollArea.nativeElement.scrollTop = 0;

    this.load();
  }
  load(retry = false): void {
    if (this.loading() || !this.hasNext() || (this.error() && !retry)) 
      return;

    this.error.set(''); this.loading.set(true);
    this.request = this.imports.history(this.cursor).subscribe({
      next: page => {
        const previous = this.cursor;
        const next = page.nextCursorId != null && page.nextCursorCreatedAt ? { id: page.nextCursorId, createdAt: page.nextCursorCreatedAt } : null;
        const known = new Set(this.rows().map(row => row.id));

        this.rows.update(rows => [...rows, ...page.content.filter(row => !known.has(row.id))]);
        this.cursor = next;
        this.hasNext.set(page.hasNext);
        this.loading.set(false);

        if (page.hasNext && (!next || !page.content.length || (next.id === previous?.id && next.createdAt === previous?.createdAt))) {
          this.hasNext.set(false); this.error.set('Сервер вернул некорректный курсор истории. Обновите список.'); return;
        }

        this.timer = setTimeout(() => this.checkViewport(), 0);
      },
      error: error => { 
        this.loading.set(false); 
        this.error.set(this.errors.message(error)); 
      },
    });
  }
  checkViewport(): void {
    const el = this.scrollArea?.nativeElement;
    if (el && el.scrollHeight - el.scrollTop - el.clientHeight < 180) 
      this.load();
  }
  status(value: string): string { return ({ SUCCESS: 'Успешно', FAILED: 'Ошибка', IN_PROGRESS: 'В процессе', PENDING: 'В очереди', PROCESSING: 'В процессе' } as Record<string, string>)[value] ?? value; }
  
  ngOnDestroy(): void { 
    this.request?.unsubscribe(); 
    this.observer?.disconnect(); 
    clearTimeout(this.timer); 
  }
}
