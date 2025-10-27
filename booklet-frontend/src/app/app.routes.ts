

import { inject, PLATFORM_ID } from '@angular/core';
import { Router, Routes, CanActivateFn } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './services/auth.service';

import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/auth/login/login.component';
import { CatalogoGeneraleComponent } from './components/catalogo-generale/catalogo-generale.component';
import { CatalogoRedazioneComponent } from './components/catalogo-redazione/catalogo-redazione.component';
import { CatalogoPersonaleComponent } from './components/catalogo-personale/catalogo-personale.component';
import { CarrelloComponent } from './components/carrello/carrello.component';

export const authGuard: CanActivateFn = async (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);
  const auth = inject(AuthService);

  if (!isBrowser) return true;

  const logged = await auth.isLoggedIn();
  if (!logged) {
    await auth.login(state.url || '/');
    return false;
  }

  const requiredRole = route.data?.['role'] as string | undefined;
  if (requiredRole) {
    const roles = auth.getRoles();
    if (!roles.includes(requiredRole)) {
      return router.parseUrl('/home');
    }
  }
  return true;
};

export const routes: Routes = [
  { path: '', component: CatalogoGeneraleComponent },
  { path: 'home', component: HomeComponent },
  { path: 'catalogo', component: CatalogoGeneraleComponent },
  { path: 'redazione', component: CatalogoRedazioneComponent, canActivate: [authGuard], data: { role: 'REDAZIONE' } },
  { path: 'login', component: LoginComponent },
  { path: 'personale/:utenteId', component: CatalogoPersonaleComponent, canActivate: [authGuard] },
  { path: 'carrello', component: CarrelloComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'home' },
];
