import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
// Update the path below if the actual location is different
import { CarrelloService } from '../../services/carrello.services';
import { Libro } from '../../models/libro';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './libro-card.component.html',
  styleUrl: './libro-card.component.css'
})
export class LibroCardComponent {
  @Input() libro!: Libro;

  constructor(private carrelloService: CarrelloService) {}

  aggiungi() {
    this.carrelloService.aggiungiAlCarrello(this.libro).subscribe({
      next: () => alert(`"${this.libro.titolo}" aggiunto al carrello!`),
      error: (err) => alert('Errore: ' + err.message)
    });
  }
}