import { Component, computed, inject } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, RouterLink, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  isAuth = this.auth.isAuthenticatedSig;

  login() {
    // navigate to /login which triggers redirect; keep async promise unhandled
    window.location.href = '/login';
  }

  logout() {
    this.auth.logout();
  }

  myCatalogoLink(): string[] | null {
    const user = this.auth.getUser();
    if (!user?.sub) return null;
    return ['/personale', user.sub];
  }
}
