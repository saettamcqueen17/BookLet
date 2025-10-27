import { Component, Input } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CarrelloService } from '../../services/carrello.service';

export interface Libro {
  isbn: string;
  titolo: string;
  autore?: string;
  prezzo?: number;
  immagineCopertina?: string;
}

@Component({
  selector: 'app-libro-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, CurrencyPipe],
  template: `
    <mat-card class="libro-card">
      <img *ngIf="libro.immagineCopertina" mat-card-image [src]="libro.immagineCopertina" [alt]="libro.titolo" />
      <mat-card-title>{{ libro.titolo }}</mat-card-title>
      <mat-card-subtitle>{{ libro.autore }}</mat-card-subtitle>

      <mat-card-content>
        <p *ngIf="libro.prezzo != null">
          <strong>Prezzo:</strong> {{ libro.prezzo | currency:'EUR':'symbol' }}
        </p>
      </mat-card-content>

      <mat-card-actions align="end">
        <button mat-raised-button color="primary" (click)="aggiungiAlCarrello()">
          <mat-icon>add_shopping_cart</mat-icon>
          Aggiungi al carrello
        </button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: [`
    .libro-card { max-width: 360px; }
  `]
})
export class LibroCardComponent {
  @Input() libro!: Libro;

  constructor(private carrelloService: CarrelloService) {}

  aggiungiAlCarrello(): void {
    if (!this.libro?.isbn) return;
    this.carrelloService.aggiungiAlCarrello(this.libro.isbn).subscribe();
  }
}
