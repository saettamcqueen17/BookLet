import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Libro } from '../models/libro';

@Injectable({ providedIn: 'root' })
export class CarrelloService {

  private apiUrl = `${environment.apiBase}/api/me/carrello`;

  constructor(private http: HttpClient) {}

  getCarrello(): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.apiUrl);
  }

  aggiungiAlCarrello(libro: Libro): Observable<void> {
    return this.http.post<void>(this.apiUrl, libro);
  }

  rimuoviDalCarrello(isbnLibro: String): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${isbnLibro}`);
  }
}
