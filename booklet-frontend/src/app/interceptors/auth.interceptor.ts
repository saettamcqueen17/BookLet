// file: 'booklet-frontend/src/app/interceptors/auth.interceptor.ts'
import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/auth.service';

const bearerExcluded: (string | RegExp)[] = [
  /^\/?assets\//,
  /\/public\//
];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  // ❌ SSR → nessun token
  if (!isBrowser) return next(req);

  const url = req.url;
  const skip = bearerExcluded.some(p =>
    typeof p === 'string'
      ? url.includes(p)
      : p.test(url)
  );

  // ❌ endpoints esclusi
  if (skip) return next(req);

  // 🔥 leggiamo SEMPRE il token reale dal sessionStorage
  const token = sessionStorage.getItem('kc_token');

  if (!token) {
    // ⚠ nessun token → richiesta anonima
    return next(req);
  }

  // 🔥 aggiungiamo header Authorization con Bearer <token>
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};
