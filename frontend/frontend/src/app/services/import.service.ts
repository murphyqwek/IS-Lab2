import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_CONFIG } from '../core/api.config';
export const MAX_IMPORT_BYTES = 1024 * 1024;
export function validateImportFile(file: File): string | null {
  if (file.size === 0) return 'Файл пустой. Выберите непустой JSON-файл.';
  if (file.size >= MAX_IMPORT_BYTES) return 'Размер файла должен быть меньше 1 МБ (1 048 576 байт).';
  return null;
}
export interface ImportHistory { id: number; username: string; importedObjectsCount: number; createdAt: string; status: string; }
export interface HistoryPage { content: ImportHistory[]; nextCursorId: number | null; nextCursorCreatedAt: string | null; hasNext: boolean; }
export interface HistoryCursor { id: number; createdAt: string; }
@Injectable({ providedIn: 'root' })
export class ImportService {
  private readonly http = inject(HttpClient);
  readonly historyOpen = signal(false);
  readonly revision = signal(0);
  readonly notice = signal<{ text: string; error: boolean } | null>(null);
  upload(file: File) { const body = new FormData(); body.append('file', file); return this.http.post<void>(API_CONFIG.import, body); }
  history(cursor: HistoryCursor | null) {
    let params = new HttpParams().set('size', 5);
    if (cursor) params = params.set('cursorId', cursor.id).set('cursorCreatedAt', cursor.createdAt);
    return this.http.get<HistoryPage>(API_CONFIG.history, { params });
  }
}
