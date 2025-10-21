import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CarrelloService } from '../../services/carrello.services';
import { Libro } from '../../models/libro';

@Component({
  selector: 'app-carrello',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './carrello.html',
  styleUrl: './carrello.css'
})
export class CarrelloComponent implements OnInit {

  libri: Libro[] = [];
  caricamento = true;

  constructor(private carrelloService: CarrelloService) {}

  ngOnInit(): void {
    this.caricaCarrello();
  }

  caricaCarrello() {
    this.caricamento = true;
    this.carrelloService.getCarrello().subscribe({
      next: (data) => {
        this.libri = data;
        this.caricamento = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento carrello:', err);
        this.caricamento = false;
      }
    });
  }

  rimuovi(libro: Libro) {
    this.carrelloService.rimuoviDalCarrello(libro.isbn).subscribe({
      next: () => {
        this.libri = this.libri.filter(l => l.isbn !== libro.isbn);
      },
      error: (err) => alert('Errore durante la rimozione: ' + err.message)
    });
  }
}
