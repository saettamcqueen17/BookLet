import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Libro } from '../models/libro';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RedazioneService {

  private base = `${environment.apiBase}/api/redazione`;

  constructor(private http: HttpClient) {}

  getCatalogo(): Observable<Libro[]> {
    return this.http.get<Libro[]>(this.base);
  }

  aggiungiLibro(isbn: string): Observable<void> {
    return this.http.post<void>(`${this.base}/${isbn}`, {});
  }

  rimuoviLibro(isbn: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${isbn}`);
  }
}
