import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  MatButtonModule
} from '@angular/material/button';
import {
  MatFormFieldModule
} from '@angular/material/form-field';
import {
  MatInputModule
} from '@angular/material/input';
import {
  MatIconModule
} from '@angular/material/icon';

@Component({
  selector: 'app-redazione-review-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule
  ],
  template: `
    <h2 style="margin-bottom: 15px;">
      Modifica recensione — {{data.titolo}}
    </h2>

    <div class="stars" style="margin-bottom: 15px; font-size: 28px; cursor: pointer;">
      <mat-icon
        *ngFor="let star of [1,2,3,4,5]"
        (click)="rating = star"
        [ngClass]="{'filled': star <= rating}"
        style="color: #ffca28; margin-right: 5px;"
      >
        {{ star <= rating ? 'star' : 'star_border' }}
      </mat-icon>
    </div>

    <mat-form-field appearance="outline" style="width: 100%; margin-bottom: 20px;">
      <mat-label>Recensione</mat-label>
      <textarea matInput [(ngModel)]="recensione" rows="5"></textarea>
    </mat-form-field>

    <div style="display:flex; justify-content: flex-end; gap: 10px;">
      <button mat-button (click)="dialogRef.close()">Annulla</button>
      <button mat-raised-button color="primary" (click)="save()">Salva</button>
    </div>
  `,
  styles: [`
    .filled {
      color: #ffca28 !important;
    }
  `]
})
export class RedazioneReviewDialogComponent {

  rating: number = 0;
  recensione: string = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      isbn: string;
      titolo: string;
      autore: string;
      rating: number | null;
      recensione: string;
    },
    public dialogRef: MatDialogRef<RedazioneReviewDialogComponent>
  ) {
    this.rating = data.rating ?? 0;
    this.recensione = data.recensione ?? '';
  }

  save() {
    this.dialogRef.close({
      recensione: this.recensione,
      valutazione: this.rating
    });
  }
}
