import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6 text-center">
      <h2>Completamento login…</h2>
      <p>Verifica delle credenziali in corso.</p>
    </div>
  `
})
export class AuthCallbackComponent implements OnInit {
  constructor(private auth: AuthService) {}
  async ngOnInit() {
    await this.auth.handleAuthCallback();
  }
}

