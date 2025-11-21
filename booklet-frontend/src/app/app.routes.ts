

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
import {GestioneRedazioneComponent} from './components/catalogo-redazione/gestione-redazione/gestione-redazione.component';

export const authGuard: CanActivateFn = async (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);
  const isBrowser = isPlatformBrowser(platformId);
  const auth = inject(AuthService);

  if (!isBrowser) return true;

  return auth.isLoggedIn().then((logged) => {
    if (!logged) {
      return router.parseUrl('/login');

    }

    const requiredRole = route.data?.['role'] as string | undefined;
    if (requiredRole) {
      const roles = auth.getRolesAsync();

    }
    return true;
  });
}

  export const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'home', component: HomeComponent},
    {path: 'catalogo-generale', component: CatalogoGeneraleComponent},

    {
      path: 'redazione',
      component: CatalogoRedazioneComponent
    },


    {path: 'login', component: LoginComponent},
    {path: 'catalogo-personale', component: CatalogoPersonaleComponent},
    {path: 'carrello', component: CarrelloComponent },
    {path: '**', redirectTo: 'home'}];

