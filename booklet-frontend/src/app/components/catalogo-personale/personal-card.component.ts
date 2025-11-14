import { Component, Input, inject } from '@angular/core';
import { CommonModule, DatePipe, NgIf, NgFor } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { CatalogoPersonaleDTO } from '../../models/catalogo-personale';
import { CatalogoPersonaleService } from '../../services/catalogoPersonale.service';
import { ReviewDialogComponent } from '../../components/catalogo-personale/ReviewDialogComponent';

@Component({
  selector: 'app-personal-book-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatDialogModule,
    DatePipe,
    NgIf,
    NgFor// 👈 utile per evitare warning "unused"
  ],
  template: `
    <mat-card class="cp-card">

      <mat-card-header>
        <mat-card-title>{{ book.titolo }}</mat-card-title>
        <mat-card-subtitle>{{ book.autore }}</mat-card-subtitle>

        <!-- MENU AZIONI -->
        <button mat-icon-button [matMenuTriggerFor]="menu">
          <mat-icon>more_vert</mat-icon>
        </button>

        <mat-menu #menu="matMenu">
          <button mat-menu-item (click)="openReviewDialog()">
            <mat-icon>edit</mat-icon> Modifica recensione
          </button>

          <button mat-menu-item (click)="cambiaScaffale()">
            <mat-icon>bookmark</mat-icon> Cambia stato lettura
          </button>
        </mat-menu>
      </mat-card-header>

      <mat-card-content>

        <div class="meta">
          <span class="chip">{{ book.scaffale }}</span>
          <span class="isbn">ISBN: {{ book.isbn }}</span>
        </div>

        <div class="rating" *ngIf="book.rating as r">
          <mat-icon *ngFor="let s of stars(r)">star</mat-icon>
          <mat-icon *ngFor="let s of emptyStars(r)">star_border</mat-icon>
          <span class="rating-text">{{ r }}/5</span>
        </div>

        <p class="recensione" *ngIf="book.recensione">{{ book.recensione }}</p>
        <p class="added">Aggiunto: {{ book.addedAt | date:'mediumDate' }}</p>

      </mat-card-content>
    </mat-card>
  `,
  styles: [
    `.cp-card{ display:block; position:relative; }`,
    `.meta{ display:flex; gap:.5rem; align-items:center; margin:.25rem 0 .5rem; }`,
    `.chip{ background:#e0e0e0; border-radius:12px; padding:2px 8px; font-size:.75rem; }`,
    `.isbn{ color:#666; font-size:.8rem; }`,
    `.rating{ display:flex; align-items:center; gap:2px; color:#ff9800; }`,
    `.rating-text{ color:#555; margin-left:.25rem; font-size:.85rem; }`,
    `.recensione{ margin:.5rem 0; color:#333; }`,
    `.added{ color:#888; font-size:.8rem; }`
  ]
})
export class PersonalBookCardComponent {
  @Input({ required: true }) book!: CatalogoPersonaleDTO;

  private dialog = inject(MatDialog);
  private service = inject(CatalogoPersonaleService);

  stars(rating: number) { return Array(Math.round(rating)).fill(0); }
  emptyStars(rating: number) { return Array(5 - Math.round(rating)).fill(0); }

  // === CAMBIO SCAFFALE (ciclico) ===
  cambiaScaffale() {
    const states = ['DaLeggere', 'StaiLeggendo', 'Finito'] as const;
    const idx = states.indexOf(this.book.scaffale as any);
    const next = states[(idx + 1) % states.length];

    this.service.updateScaffale(this.book.isbn, next).subscribe(updated => {
      this.book = updated;
    });
  }

  // === APRE IL DIALOG ===
  openReviewDialog() {
    const dialogRef = this.dialog.open(ReviewDialogComponent, {
      width: '500px',
      data: {
        isbn: this.book.isbn,
        titolo: this.book.titolo,
        autore: this.book.autore,
        rating: this.book.rating,
        recensione: this.book.recensione
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.book = result;
      }
    });
  }
}
