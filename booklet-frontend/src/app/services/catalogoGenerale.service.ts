import { Inject, Injectable, InjectionToken } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Libro } from '../models/libro';
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => ''
});
@Injectable({
  providedIn: 'root'
})
export class CatalogoGeneraleService {
  private readonly baseUrl: string;
  constructor(private readonly http: HttpClient, @Inject(API_BASE_URL) apiBaseUrl: string) {
    this.baseUrl = this.combineUrl(apiBaseUrl, '/api/catalogo');
  }
  lista(page = 0, size = 20, sort?: string): Observable<Page<Libro>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));
    if (sort) {
      params = params.set('sort', sort);
    }
    return this.http.get<Page<Libro>>(this.baseUrl, { params });
  }
  dettaglio(isbn: string): Observable<Libro> {
    return this.http.get<Libro>(`${this.baseUrl}/${encodeURIComponent(isbn)}`);
  }
  aggiungi(libro: Libro): Observable<Libro> {
    return this.http.post<Libro>(this.baseUrl, libro);
  }
  aggiungiMultipli(libri: Libro[]): Observable<Libro[]> {
    return this.http.post<Libro[]>(`${this.baseUrl}/batch`, libri);
  }
  elimina(isbn: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${encodeURIComponent(isbn)}`);
  }
  private combineUrl(base: string, path: string): string {
    if (!base) {
      return path;
    }
    if (base.endsWith('/') && path.startsWith('/')) {
      return `${base}${path.substring(1)}`;
    }
    if (!base.endsWith('/') && !path.startsWith('/')) {
      return `${base}/${path}`;
    }
    return `${base}${path}`;
  }
}