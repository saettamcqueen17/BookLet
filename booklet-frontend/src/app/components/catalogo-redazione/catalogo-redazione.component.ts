import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RedazioneService } from '../../services/redazione.service';
import { SchedaRedazione } from '../../models/catalogo-redazione';

@Component({
  selector: 'app-catalogo-redazione',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Catalogo Redazione</h2>
    <p class="muted">Mostro solo le voci visibili</p>
    <ul>
      <li *ngFor="let s of schede">
        <b>{{ s.isbn }}</b> — {{ s.categoria || 'N/D' }}
        <span *ngIf="s.valutazioneRedazione"> · ★ {{ s.valutazioneRedazione }}</span>
        <div *ngIf="s.recensione">{{ s.recensione }}</div>
      </li>
    </ul>
  `
})
export class CatalogoRedazioneComponent implements OnInit {
  schede: SchedaRedazione[] = [];
  constructor(private api: RedazioneService) {}
  ngOnInit(): void {
    this.api.getVisibili().subscribe(d => this.schede = d);
  }
}
