// TypeScript
// file: 'booklet-frontend/src/app/components/shared/navbar.component.ts'
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  constructor(private auth: AuthService, private router: Router) {}

  isAuth(): boolean {
    return this.auth.isAuthenticatedSig();
  }

  login(): void {
    void this.auth.login('/');
  }

  logout(): void {
    void this.auth.logout();
  }

  // Fix: usa 'id' invece di 'sub'
  myCatalogoLink(): string[] | null {
    const user = this.auth.getUser();
    if (!user?.id) return null;
    return ['/personale', user.id];
  }
}
