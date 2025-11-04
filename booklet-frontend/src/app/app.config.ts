import { ApplicationConfig, APP_INITIALIZER, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { KeycloakBearerInterceptor } from 'keycloak-angular';



function initializeKeycloak() {
  const keycloak = inject(KeycloakService);
  const platformId = inject(PLATFORM_ID);

  return async () => {
    if (isPlatformBrowser(platformId)) {
      await keycloak.init({
        config: {
          url: 'http://localhost:8085',
          realm: 'Booklet',
          clientId: 'booklet_client_frontend',
        },
        initOptions: {
          onLoad: 'login-required',
          checkLoginIframe: false,
        },
      });
    } else {
      console.log('Keycloak init skipped (server-side rendering)');
    }
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: KeycloakBearerInterceptor, multi: true },

    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),

    KeycloakService,

    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService], // 🔥 obbligatoria
    },
  ],
};
