import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router, NavigationEnd } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { LibroCardComponent } from '../shared/libro-card.component';

@Component({
  selector: 'app-catalogo-generale',
  standalone: true,
  imports: [CommonModule, LibroCardComponent],
  templateUrl: './catalogo-generale.component.html',
  styleUrls: ['./catalogo-generale.component.css'],
})
export class CatalogoGeneraleComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private router = inject(Router);
  private navSub?: Subscription;

  libri: any[] = [];
  caricamento = false;
  errore: string | null = null;

  ngOnInit(): void {
    this.caricaLibri();

    // (opzionale) ricarica se ritorni su /catalogo
    this.navSub = this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        if (this.router.url === '/catalogo') {
          this.caricaLibri();
        }
      });
  }

  caricaLibri(): void {
    this.caricamento = true;
    this.errore = null;

    this.http.get<any[]>('http://localhost:8080/api/catalogo/generale').subscribe({
      next: (dati) => {
        this.libri = Array.isArray(dati) ? dati : [];
        this.caricamento = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento catalogo:', err);
        this.errore = 'Impossibile caricare il catalogo. Riprova più tardi.';
        this.caricamento = false;
      },
    });
  }

  trackByIsbn(_: number, libro: any) {
    return libro?.isbn ?? libro?.id ?? _;
  }

  ngOnDestroy(): void {
    this.navSub?.unsubscribe();
  }
}
