import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { LibroCardComponent } from '../shared/libro-card.component';
import { Libro } from '../../models/libro';  // assicurati che esista il modello Libro

@Component({
  selector: 'app-catalogo-generale',
  standalone: true,
  imports: [CommonModule, LibroCardComponent],
  templateUrl: './catalogo-generale.component.html',
  styleUrls: ['./catalogo-generale.component.css']   // ⚠️ era "styleUrl" → deve essere "styleUrls"
})
export class CatalogoGeneraleComponent implements OnInit {

  libri: Libro[] = [];     // ✅ proprietà mancante

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    // ✅ chiamata API per ottenere SOLO i libri con disponibilità > 0
    this.http.get<Libro[]>('http://localhost:8080/api/catalogo/generale')
      .subscribe(data => this.libri = data);
  }
}
