import { Component, Inject, CUSTOM_ELEMENTS_SCHEMA, PLATFORM_ID, AfterViewInit } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NavbarComponent } from './components/shared/navbar.component';
import { KeycloakService } from 'keycloak-angular';
import { AuthStatusService } from './services/AuthStatusService';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatToolbarModule, NavbarComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  template: `
    <mat-toolbar class="navbar" role="navigation">
      <span style="font-weight:700">Booklet</span>
      <span class="spacer"></span>
      <app-navbar></app-navbar>
    </mat-toolbar>

    <router-outlet></router-outlet>

    <footer class="footer">
      © 2025 Booklet – Tutti i diritti riservati
    </footer>
  `,
  styles: [`
    .navbar { position: sticky; top: 0; z-index: 100; }
    .spacer { flex: 1 1 auto; }
    .footer { margin-top: 2rem; padding: 1rem; text-align: center; color: #666; }
  `]
})
export class App implements AfterViewInit {
  constructor(
    private keycloak: KeycloakService,
    private authStatus: AuthStatusService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  async ngAfterViewInit() {
    if (!isPlatformBrowser(this.platformId)) {
      console.log('ℹ️ Keycloak non inizializzato (SSR - fase server)');
      return;
    }

    try {
      // 🔹 Inizializza Keycloak
      await this.keycloak.init({
        config: {
          url: 'http://localhost:8085',
          realm: 'Booklet',
          clientId: 'booklet_client_frontend',
        },
        initOptions: {
          onLoad: 'check-sso',             // 👈 NON forza subito il login
          silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
          checkLoginIframe: false,
        },
        enableBearerInterceptor: true,
        bearerPrefix: 'Bearer',
        bearerExcludedUrls: ['/assets', '/api/public'],
      });

      const loggedIn = await this.keycloak.isLoggedIn();
      console.log('✅ Keycloak inizializzato lato browser. loggedIn:', loggedIn);

      if (!loggedIn) {
        console.log('🔁 Nessuna sessione attiva, avvio schermata di login Keycloak...');
        await this.keycloak.login({ redirectUri: window.location.origin + '/home' });
      } else {
        await this.authStatus.refreshStatus();
      }
    } catch (err) {
      console.error('❌ Errore durante init Keycloak lato browser:', err);
    }
  }
}
