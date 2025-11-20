import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Libro } from '../models/libro';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {SchedaRedazione} from '../models/catalogo-redazione';

@Injectable({ providedIn: 'root' })
export class RedazioneService {

  private base = `${environment.apiBase}/api/redazione`;

  constructor(private http: HttpClient) {}

  getCatalogo(): Observable<SchedaRedazione[]> {
    return this.http.get<SchedaRedazione[]>(this.base);
  }

  aggiungiLibro(isbn: string): Observable<void> {
    return this.http.post<void>(`${this.base}/${isbn}`, {});
  }

  rimuoviLibro(isbn: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${isbn}`);
  }
}
