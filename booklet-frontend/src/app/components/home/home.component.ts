import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { LibroCardComponent } from '../shared/libro-card.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MatIconModule, LibroCardComponent, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  private http = inject(HttpClient);

  anteprimaLibri: any[] = [];
  caricamento = false;
  errore = false;

  ngOnInit(): void {
    this.caricamento = true;
    this.http.get<any[]>('http://localhost:8080/api/catalogo/generale').subscribe({
      next: (dati) => {
        // Mostra solo i primi 6 libri come anteprima
        this.anteprimaLibri = (dati ?? []).slice(0, 6);
        this.caricamento = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento anteprima:', err);
        this.errore = true;
        this.caricamento = false;
      },
    });
  }
}
