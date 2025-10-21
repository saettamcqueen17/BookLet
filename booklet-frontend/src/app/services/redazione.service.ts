import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SchedaRedazione } from '../models/catalogo-redazione';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RedazioneService {
  private base = `${environment.apiBase}/api/redazione`;

  constructor(private http: HttpClient) {}

  getTutte(): Observable<SchedaRedazione[]> {
    return this.http.get<SchedaRedazione[]>(this.base);
  }

  getVisibili(): Observable<SchedaRedazione[]> {
    return this.http.get<SchedaRedazione[]>(`${this.base}/visibili`);
  }

  aggiungi(opts: { isbn: string; categoria: string; recensione?: string; voto?: number }): Observable<SchedaRedazione> {
    let params = new HttpParams()
      .set('isbn', opts.isbn)
      .set('categoria', opts.categoria);
    if (opts.recensione) params = params.set('recensione', opts.recensione);
    if (opts.voto != null) params = params.set('voto', String(opts.voto));
    return this.http.post<SchedaRedazione>(`${this.base}/aggiungi`, null, { params });
  }

  elimina(isbn: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${encodeURIComponent(isbn)}`);
  }
}