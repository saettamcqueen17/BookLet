// file: 'booklet-frontend/src/app/interceptors/auth.interceptor.ts'
import { HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { from, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

const bearerExcluded: (string | RegExp)[] = [/^\/?assets\//, /\/public\//];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);

  if (!isBrowser) return next(req); // SSR: non toccare la request

  const url = req.url;
  const isExcluded = bearerExcluded.some((p) =>
    typeof p === 'string' ? url.includes(p) : p.test(url)
  );
  if (isExcluded) return next(req);

  const auth = inject(AuthService);

  return from(auth.isLoggedIn()).pipe(
    switchMap((logged) => (logged ? from(auth.getToken()) : of<string | null>(null))),
    switchMap((token) => {
      const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
      return next(authReq);
    }),
    catchError(() => next(req))
  );
};
