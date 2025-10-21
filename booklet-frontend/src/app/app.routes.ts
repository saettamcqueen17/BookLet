import { Routes } from '@angular/router';
import { HomeComponent} from './components/home/home';
import { CatalogoGenerale } from './components/catalogo-generale/catalogo-generale';
import { CatalogoPersonaleComponent } from './components/catalogo-personale/catalogo-personale';
import { CatalogoRedazioneComponent } from './components/catalogo-redazione/catalogo-redazione';
import { CarrelloComponent } from './components/carrello/carrello';
import { LoginRedirectComponent } from './components/auth/login-redirect';
import { AuthCallbackComponent } from './components/auth/auth-callback';
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginRedirectComponent },
  { path: 'auth/callback', component: AuthCallbackComponent },
  { path: 'carrello', component: CarrelloComponent },
  { path: 'redazione', component: CatalogoRedazioneComponent },
  { 
  path: 'personale/:utenteId', 
  component: CatalogoPersonaleComponent, 
  data: { renderMode: 'client' }  // disabilita prerendering per rotta parametrica
},
  { path: '**', redirectTo: '' }
];
