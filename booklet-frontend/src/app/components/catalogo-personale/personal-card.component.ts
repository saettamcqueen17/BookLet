import { Component, Input } from '@angular/core';
import { CommonModule, DatePipe, NgIf, NgFor } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CatalogoPersonaleDTO } from '../../models/catalogo-personale';

@Component({
  selector: 'app-personal-book-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, DatePipe, NgIf, NgFor],
  template: `
    <mat-card class="cp-card">
      <mat-card-header>
        <mat-card-title>{{ book.titolo }}</mat-card-title>
        <mat-card-subtitle>{{ book.autore }}</mat-card-subtitle>
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
    `.cp-card{ display:block; }`,
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

  stars(rating: number) { return Array(Math.round(rating)).fill(0); }
  emptyStars(rating: number) { return Array(5 - Math.round(rating)).fill(0); }
}

