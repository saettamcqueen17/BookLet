import { APP_INITIALIZER, ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';
import { API_BASE_URL } from './services/catalogoGenerale.service';
import { environment } from '../environments/environment';
import { AuthService } from './services/auth.service';

function initAuth(auth: AuthService): () => Promise<void> {
  return () => auth.initSession();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    provideAnimations(),
    { provide: API_BASE_URL, useValue: environment.apiBase },
    { provide: API_BASE_URL, useValue: environment.apiBase },
    {
      provide: APP_INITIALIZER,
      useFactory: initAuth,
      multi: true,
      deps: [AuthService]
    }
  ]
};
