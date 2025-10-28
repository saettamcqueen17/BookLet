// file: 'booklet-frontend/src/app/guards/is-access-allowed.guard.ts'
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { from, of } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const isAccessAllowed = () => {
  const platformId = inject(PLATFORM_ID);
  if (!isPlatformBrowser(platformId)) return of(true); // SSR: consenti
  const auth = inject(AuthService);
  return from(auth.isLoggedIn());
};
