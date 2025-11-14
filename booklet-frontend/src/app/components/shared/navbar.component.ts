import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { KeycloakService } from 'keycloak-angular';
import { AuthStatusService } from '../../services/AuthStatusService';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;

  constructor(
    private keycloak: KeycloakService,
    private authStatus: AuthStatusService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.authStatus.authenticated$.subscribe(isAuth => {
      this.isLoggedIn = isAuth;
      console.log('🔁 Navbar aggiornata: isLoggedIn =', this.isLoggedIn);
    });
  }

  async login(): Promise<void> {
    await this.keycloak.login({redirectUri: window.location.origin + '/home'});
    await this.authStatus.refreshStatus();
  }

  async logout(): Promise<void> {
    await this.keycloak.logout(window.location.origin + '/home');
    this.authStatus.setAuthenticated(false);
    await this.router.navigate(['/home']);
  }

  myCatalogoLink(): string {
    return '/catalogo-personale';
  }
}
