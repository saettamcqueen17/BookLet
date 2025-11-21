import { Component, AfterViewInit, Inject, PLATFORM_ID, inject } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavbarComponent } from './components/shared/navbar.component';

import { AuthService } from './services/auth.service';
import { AuthStateService } from './services/AuthStatusService';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatToolbarModule, NavbarComponent],
  template: `
    <mat-toolbar class="navbar">
      <span style="font-weight:700">Booklet</span>
      <span class="spacer"></span>
      <app-navbar></app-navbar>
    </mat-toolbar>

    <router-outlet></router-outlet>

    <footer class="footer">
      © 2025 Booklet – Tutti i diritti riservati
    </footer>
  `,
})
export class App implements AfterViewInit {

  private auth = inject(AuthService);
  private authState = inject(AuthStateService);

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  async ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    const logged = await this.auth.isLoggedIn();
    this.authState.setLogged(logged);

    if (!logged) {
      await this.auth.login('/home');
    }
  }
}
