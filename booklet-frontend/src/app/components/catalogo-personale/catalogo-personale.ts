import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CatalogoPersonaleService } from '../../services/catalogoPersonale.services';
import { CatalogoPersonaleContainerDTO } from '../../models/catalogo-personale';
import { PersonalBookCardComponent } from './personal-card.component';

@Component({
  selector: 'app-catalogo-personale',
  standalone: true,
  imports: [CommonModule, PersonalBookCardComponent],
  template: `
    <section class="cp-header">
      <h2>Catalogo Personale</h2>
      <div *ngIf="data as d" class="cp-meta">
        <span>Utente: {{ d.username || d.utenteId }}</span>
        <span>Totale libri: {{ d.totaleLibri }}</span>
      </div>
    </section>

    <ng-container *ngIf="loading">Caricamento…</ng-container>
    <ng-container *ngIf="error">Errore nel caricamento: {{ error }}</ng-container>

    <div class="cp-grid" *ngIf="data as d">
      <app-personal-book-card *ngFor="let b of d.libri" [book]="b" />
    </div>
  `,
  styles: [
    `.cp-header{ display:flex; align-items:baseline; justify-content:space-between; gap:1rem; }`,
    `.cp-meta{ color:#666; display:flex; gap:1rem; }`,
    `.cp-grid{ display:grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 12px; }`
  ]
})
export class CatalogoPersonaleComponent implements OnInit {
  data: CatalogoPersonaleContainerDTO | null = null;
  loading = false;
  error: string | null = null;
  constructor(private route: ActivatedRoute, private api: CatalogoPersonaleService) {}
  ngOnInit(): void {
    const utenteId = this.route.snapshot.paramMap.get('utenteId')!;
    this.loading = true;
    this.api.getCatalogo(utenteId).subscribe({
      next: (d) => { this.data = d; this.error = null; },
      error: (err) => { this.error = err?.message ?? 'Errore sconosciuto'; },
      complete: () => { this.loading = false; }
    });
  }
}

