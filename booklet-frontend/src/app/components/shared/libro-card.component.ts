// TypeScript
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
  casaEditrice?: string;
  categoria?: string;
  disponibilita?: number;
}

@Component({
  selector: 'app-libro-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, CurrencyPipe],
  templateUrl: './libro-card.component.html',
  styles: [`
    .libro-card { max-width: 360px; }
  `]
})
export class LibroCardComponent {
  @Input() libro!: Libro;

  aggiunto = false;

  constructor(private carrelloService: CarrelloService) {}

  aggiungiAlCarrello(): void {
    if (!this.libro?.isbn) return;
    if ((this.libro.disponibilita ?? 0) === 0) return;

    this.carrelloService.aggiungiAlCarrello(this.libro.isbn).subscribe({
      next: () => {
        this.aggiunto = true;
        setTimeout(() => (this.aggiunto = false), 2000);
      }
    });
  }
}
