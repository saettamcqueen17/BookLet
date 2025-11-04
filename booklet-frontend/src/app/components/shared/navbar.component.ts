import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { KeycloakService } from 'keycloak-angular';

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
  isLoggedIn = false; // ✅ valore stabile, non promesse

  constructor(private keycloak: KeycloakService) {}

  async ngOnInit() {
    this.isLoggedIn = await this.keycloak.isLoggedIn();
  }

  async login(): Promise<void> {
    await this.keycloak.login({ redirectUri: window.location.origin + '/home' });
  }

  async logout(): Promise<void> {
    await this.keycloak.logout(window.location.origin + '/home');
  }

  myCatalogoLink(): string | null {
    return '/catalogo-personale';
  }
}
