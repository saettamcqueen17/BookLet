import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatButtonModule } from '@angular/material/button';

import { RedazioneService } from '../../../services/redazione.service';
import { CatalogoGeneraleService } from '../../../services/catalogoGenerale.service';

import { Libro } from '../../../models/libro';
import { SchedaRedazione } from '../../../models/catalogo-redazione';

import { SchedaRedazioneComponent } from '../scheda-redazione/scheda-redazione.component';

@Component({
  selector: 'app-gestione-redazione',
  standalone: true,
  templateUrl: './gestione-redazione.component.html',
  styleUrls: ['./gestione-redazione.component.css'],
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatSlideToggleModule,
    SchedaRedazioneComponent
  ]
})
export class GestioneRedazioneComponent implements OnInit {

  tuttiLibri: Libro[] = [];
  redazione: SchedaRedazione[] = [];

  constructor(
    private redazioneService: RedazioneService,
    private libroService: CatalogoGeneraleService
  ) {}

  ngOnInit(): void {
    this.caricaDati();
  }

  caricaDati() {
    this.libroService.getTutti().subscribe((libri: Libro[]) => {
      this.tuttiLibri = libri;
    });

    this.redazioneService.getCatalogo().subscribe((libri: SchedaRedazione[]) => {
      this.redazione = libri;
    });
  }

  aggiungi(isbn: string) {
    this.redazioneService.aggiungiLibro(isbn).subscribe(() => this.caricaDati());
  }

  rimuovi(isbn: string) {
    this.redazioneService.rimuoviLibro(isbn).subscribe(() => this.caricaDati());
  }

  modificaRecensione(s: SchedaRedazione) {
    console.log("Modifica recensione", s);
  }

  toggleVisibile(s: SchedaRedazione) {
    const nuovo = !s.visibile;

    this.redazioneService.updateVisibile(s.isbn, nuovo).subscribe({
      next: () => s.visibile = nuovo,
      error: err => console.error("Errore toggle visibile", err)
    });
  }

  isInRedazione(isbn: string): boolean {
    return this.redazione.some(s => s.isbn === isbn);
  }
}
