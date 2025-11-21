import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';

import { AuthService } from '../../services/auth.service';
import { AuthStateService } from '../../services/AuthStatusService';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatToolbarModule, MatButtonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  isLoggedIn = false;

  private auth = inject(AuthService);
  private authState = inject(AuthStateService);
  private router = inject(Router);

  ngOnInit(): void {
    this.authState.logged$.subscribe(isLogged => {
      this.isLoggedIn = isLogged;
    });
  }

  login() {
    return this.auth.login('/home');
  }

  async logout() {
    await this.auth.logout();
    this.authState.setLogged(false);
    this.router.navigate(['/home']);
  }
}
