import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { LibroCardComponent } from '../shared/libro-card.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, RouterOutlet, LibroCardComponent,CommonModule, MatIconModule,],
  template: `
    <nav class="p-3 flex gap-3">
      <a routerLink="/redazione">Redazione</a>
      <a routerLink="/carrello">Carrello</a>
      <!-- metti il tuo UUID qui o leggilo da Keycloak -->
      <a [routerLink]="['/personale', demoUser]">Catalogo Personale</a>
    </nav>
    <router-outlet />
  `
})
export class HomeComponent {
  demoUser = '00000000-0000-0000-0000-000000000000';
}
