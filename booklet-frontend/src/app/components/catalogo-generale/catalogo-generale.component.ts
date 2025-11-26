

import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatIconModule } from '@angular/material/icon';

import { LibroCardComponent } from '../shared/libro-card.component';
import { DialogAggiungiLibroComponent } from '../catalogo-generale/dialog-aggiungi-libro.component';

import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-catalogo-generale',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LibroCardComponent,

    // Angular Material
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSidenavModule,
    MatListModule,
    MatRadioModule,
    MatIconModule
  ],
  templateUrl: './catalogo-generale.component.html',
  styleUrls: ['./catalogo-generale.component.css'],
})
export class CatalogoGeneraleComponent implements OnInit, OnDestroy {

  private http = inject(HttpClient);
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private auth = inject(AuthService);


  libriOriginali: any[] = [];
  libriFiltrati: any[] = [];


  generi: string[] = [];
  filtroTitolo: string = '';
  filtroGenere: string[] = [];
  ordinamento: string = 'titolo';


  caricamento = false;
  errore: string | null = null;


  ruoloAdmin = false;
  isbnDaRimuovere: string = '';

  private navSub?: Subscription;

  ngOnInit(): void {
    this.caricaLibri();
    this.caricaGeneri();

    this.auth.isLoggedIn().then(async (logged) => {
      if (logged) {
        const roles = await this.auth.getRolesAsync();
        this.ruoloAdmin = roles.includes('ADMIN');
      }
    });
  }


  ngOnDestroy(): void {
    this.navSub?.unsubscribe();
  }

  caricaLibri() {
    this.caricamento = true;
    this.errore = null;

    this.http.get<any[]>(`${environment.apiBase}/api/catalogo/generale`)
      .subscribe({
        next: (data) => {
          this.libriOriginali = data || [];
          this.libriFiltrati = [...this.libriOriginali];
          this.applicaFiltri();
          this.caricamento = false;
        },
        error: (err) => {
          console.error('Errore caricamento catalogo:', err);
          this.errore = 'Impossibile caricare il catalogo.';
          this.caricamento = false;
        }
      });
  }

  caricaGeneri() {
    this.http.get<string[]>(`${environment.apiBase}/api/catalogo/generi`)
      .subscribe({
        next: (lista) => this.generi = lista,
        error: (err) => console.error('Errore caricamento generi:', err)
      });
  }

  applicaFiltri() {
    let risultato = [...this.libriOriginali];

    if (this.filtroTitolo.trim() !== '') {
      const query = this.filtroTitolo.toLowerCase();
      risultato = risultato.filter(l =>
        (l.titolo || '').toLowerCase().includes(query)
      );
    }

    if (this.filtroGenere.length > 0) {
      risultato = risultato.filter(l =>
        this.filtroGenere.includes(l.genere)
      );
    }

    switch (this.ordinamento) {
      case 'titolo':
        risultato.sort((a, b) => (a.titolo || '').localeCompare(b.titolo || ''));
        break;
      case 'prezzo':
        risultato.sort((a, b) => Number(a.prezzo) - Number(b.prezzo));
        break;
      case 'prezzoDesc':
        risultato.sort((a, b) => Number(b.prezzo) - Number(a.prezzo));
        break;
    }

    this.libriFiltrati = risultato;
  }

  trackByIsbn(_: number, libro: any) {
    return libro?.isbn ?? libro?.id ?? _;
  }

  apriDialogAggiungiLibro() {
    const ref = this.dialog.open(DialogAggiungiLibroComponent, {
      width: '450px',
    });

    ref.afterClosed().subscribe(r => {
      if (r === 'added') {
        this.caricaLibri();
      }
    });
  }

  rimuoviLibro() {
    const isbn = this.isbnDaRimuovere.trim();
    if (!isbn) {
      alert('Inserisci un ISBN valido!');
      return;
    }

    if (!confirm(`Sei sicuro di voler rimuovere il libro con ISBN ${isbn}?`)) {
      return;
    }

    this.http.delete(`${environment.apiBase}/api/catalogo/${isbn}`)
      .subscribe({
        next: () => {
          alert('Libro rimosso con successo!');
          this.isbnDaRimuovere = '';
          this.caricaLibri();
        },
        error: (err) => {
          alert('Errore durante la rimozione.');
          console.error(err);
        }
      });
  }
}


