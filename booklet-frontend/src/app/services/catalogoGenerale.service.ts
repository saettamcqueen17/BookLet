import { Inject, Injectable, InjectionToken } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Libro } from '../models/libro';

// ===== INTERFACCIA PAGINATA =====
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// ===== INJECTION TOKEN (facoltativo, ma coerente col tuo stile) =====
export const API_BASE_URL = new InjectionToken<string>('API_BASE_URL', {
  providedIn: 'root',
  factory: () => environment.apiBase  // ✅ ora prende l’URL corretto (http://localhost:8080)
});

// ===== SERVIZIO PRINCIPALE =====
@Injectable({
  providedIn: 'root'
})
export class CatalogoGeneraleService {

  private readonly baseUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(API_BASE_URL) apiBaseUrl: string
  ) {
    // ✅ Costruisce correttamente l’URL assoluto: http://localhost:8080/api/catalogo
    this.baseUrl = this.combineUrl(apiBaseUrl, '/api/catalogo');
    console.log('✅ [CatalogoGeneraleService] Base URL:', this.baseUrl);
  }

  // === Lista paginata di libri ===
  lista(page = 0, size = 20, sort?: string): Observable<Page<Libro>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));

    if (sort) params = params.set('sort', sort);

    console.log('📚 [CatalogoGeneraleService] GET lista con params:', params.toString());
    return this.http.get<Page<Libro>>(this.baseUrl, { params });
  }

  // === Dettaglio singolo libro ===
  dettaglio(isbn: string): Observable<Libro> {
    const url = `${this.baseUrl}/${encodeURIComponent(isbn)}`;
    console.log('📘 [CatalogoGeneraleService] GET dettaglio →', url);
    return this.http.get<Libro>(url);
  }

  // === Aggiungi un libro ===
  aggiungi(libro: Libro): Observable<Libro> {
    console.log('➕ [CatalogoGeneraleService] POST libro:', libro);
    return this.http.post<Libro>(this.baseUrl, libro);
  }

  // === Aggiungi multipli ===
  aggiungiMultipli(libri: Libro[]): Observable<Libro[]> {
    const url = `${this.baseUrl}/batch`;
    console.log('📦 [CatalogoGeneraleService] POST multipli →', url);
    return this.http.post<Libro[]>(url, libri);
  }

  // === Elimina ===
  elimina(isbn: string): Observable<void> {
    const url = `${this.baseUrl}/${encodeURIComponent(isbn)}`;
    console.log('🗑️ [CatalogoGeneraleService] DELETE →', url);
    return this.http.delete<void>(url);
  }

  // === Combina i path evitando doppi / ===
  private combineUrl(base: string, path: string): string {
    if (!base) return path;
    if (base.endsWith('/') && path.startsWith('/')) {
      return `${base}${path.substring(1)}`;
    }
    if (!base.endsWith('/') && !path.startsWith('/')) {
      return `${base}/${path}`;
    }
    return `${base}${path}`;
  }
}
