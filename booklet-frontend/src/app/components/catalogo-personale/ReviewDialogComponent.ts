import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule
} from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { CatalogoPersonaleService } from '../../services/catalogoPersonale.service';
import { CatalogoPersonaleDTO } from '../../models/catalogo-personale';

@Component({
  standalone: true,
  selector: 'app-review-dialog',
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  template: `
    <h2 mat-dialog-title>
      {{ data.titolo }}
      <div class="autore">{{ data.autore }}</div>
    </h2>

    <div mat-dialog-content>
      <div class="stars">
        <mat-icon *ngFor="let s of [1,2,3,4,5]"
                  (click)="rating = s"
                  [class.active]="s <= rating">
          star
        </mat-icon>
      </div>

      <mat-form-field appearance="outline" class="full">
        <mat-label>Recensione</mat-label>
        <textarea matInput rows="6" [(ngModel)]="recensione"></textarea>
      </mat-form-field>
    </div>

    <div mat-dialog-actions align="end">
      <button mat-button (click)="close()">Annulla</button>
      <button mat-raised-button color="primary" (click)="save()">Salva</button>
    </div>
  `,
  styles: [
    `.stars{ display:flex; gap:4px; color:#ccc; cursor:pointer; font-size:28px; margin-bottom:1rem; }`,
    `.stars .active{ color:#ff9800; }`,
    `.full{ width:100%; }`,
    `.autore{ font-size:.9rem; color:#666; margin-top:.25rem; }`
  ]
})
export class ReviewDialogComponent {
  rating: number;
  recensione: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      isbn: string;
      titolo: string;
      autore: string;
      rating: number | null;
      recensione: string | null;
    },
    private ref: MatDialogRef<ReviewDialogComponent>,
    private service: CatalogoPersonaleService
  ) {
    this.rating = data.rating ?? 0;
    this.recensione = data.recensione ?? '';
  }

  close() {
    this.ref.close();
  }

  save() {
    this.service.updateRecensione(this.data.isbn, this.recensione, this.rating)
      .subscribe((updated: CatalogoPersonaleDTO) => {
        this.ref.close(updated); // ritorna la versione aggiornata alla card
      });
  }
}
