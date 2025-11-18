import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RedazioneService } from '../../services/redazione.service';
import { SchedaRedazione } from '../../models/catalogo-redazione';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-catalogo-redazione',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Catalogo Redazione</h2>
    <p class="muted">Mostro solo le voci visibili</p>

    <button
      *ngIf="auth.getRoles().includes('REDAZIONE')"
      mat-raised-button
      color="accent"
      routerLink="/redazione/gestione">
      Gestione Redazione
    </button>

    <ul>
      <li *ngFor="let s of schede">
        <b>{{ s.isbn }}</b> — {{ s.genere || 'N/D' }}
        <span *ngIf="s.valutazioneRedazione"> · ★ {{ s.valutazioneRedazione }}</span>
        <div *ngIf="s.recensione">{{ s.recensione }}</div>
      </li>
    </ul>
  `
})
export class CatalogoRedazioneComponent implements OnInit {
  schede: SchedaRedazione[] = [];

  constructor(
    private api: RedazioneService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.api.getCatalogo().subscribe({
      next: (res) => this.schede = res,
      error: (err) => console.error('Errore nel caricamento:', err)
    });
  }
}
