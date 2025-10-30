import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import {NavbarComponent} from './components/shared/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatToolbarModule, NavbarComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  template: `
    <mat-toolbar class="navbar" role="navigation">
      <span style="font-weight:700">BookLet</span>
      <span class="spacer"></span>
      <app-navbar></app-navbar>
    </mat-toolbar>

    <router-outlet></router-outlet>

    <footer class="footer">
      © 2025 BookLet – Tutti i diritti riservati
    </footer>
  `,
  styles: [`
    .navbar { position: sticky; top: 0; z-index: 100; }
    .spacer { flex: 1 1 auto; }
    .footer { margin-top: 2rem; padding: 1rem; text-align: center; color: #666; }
  `]
})
export class App {}
