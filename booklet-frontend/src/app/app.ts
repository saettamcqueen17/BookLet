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
    <app-navbar></app-navbar>

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

    // 🔥 Inizializza Keycloak DAVVERO prima del resto
    await this.auth.isLoggedIn();

    // 🔥 Ora Keycloak è inizializzato
    const logged = this.authState.logged;
    if (!logged) {
      await this.auth.login('/home');
      return;
    }

    console.log("Keycloak completamente inizializzato");
  }

}

