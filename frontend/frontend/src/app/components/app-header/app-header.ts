import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { RealtimeService } from '../../services/realtime.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './app-header.html',
  styleUrl: './app-header.scss',
})
export class AppHeaderComponent {
  constructor(readonly realtime: RealtimeService) {}
}
