import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login-redirect',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6 text-center">
      <h2>Reindirizzamento al login…</h2>
      <p>Attendere, verrai inviato a Keycloak.</p>
    </div>
  `
})
export class LoginRedirectComponent implements OnInit {
  constructor(private auth: AuthService) {}
  async ngOnInit() {
    await this.auth.login('/');
  }
}

