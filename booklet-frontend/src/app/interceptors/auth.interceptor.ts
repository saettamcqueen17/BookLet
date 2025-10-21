import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { from } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  return from(auth.getAuthorizationHeader()).pipe(
    mergeMap((authHeader) => {
      const authReq = authHeader ? req.clone({ setHeaders: { Authorization: authHeader } }) : req;
      return next(authReq);
    })
  );
};

