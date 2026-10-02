import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppHeaderComponent } from './components/app-header/app-header';
import { ImportHistoryComponent } from './components/import-history/import-history';
import { ImportService } from './services/import.service';
@Component({ selector: 'app-root', imports: [RouterOutlet, AppHeaderComponent, ImportHistoryComponent], templateUrl: './app.html', styleUrl: './app.scss' })
export class App {
  readonly imports = inject(ImportService);
}
