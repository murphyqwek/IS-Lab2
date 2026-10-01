import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiErrorService {
  message(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return error instanceof Error ? error.message : 'Неизвестная ошибка';
    }

    if (error.status === 0) {
      return 'Сервер недоступен. Проверь, что Spring-приложение запущено и CORS настроен.';
    }

    const body = error.error;

    if (typeof body === 'string' && body.trim()) {
      return body;
    }

    if (body?.message) {
      return String(body.message);
    }

    if (Array.isArray(body?.errors)) {
      return body.errors
        .map((item: any) => item?.defaultMessage ?? item?.message ?? String(item))
        .join('\n');
    }

    if (body?.errors && typeof body.errors === 'object') {
      return Object.entries(body.errors)
        .map(([field, message]) => `${field}: ${Array.isArray(message) ? message.join(', ') : message}`)
        .join('\n');
    }

    return `Ошибка HTTP ${error.status}${error.statusText ? `: ${error.statusText}` : ''}`;
  }
}
