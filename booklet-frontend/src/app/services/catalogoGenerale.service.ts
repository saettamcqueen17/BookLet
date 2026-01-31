import { Inject, Injectable, InjectionToken } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
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
  factory: () => environment.apiBase
});

@Injectable({
  providedIn: 'root'
})
export class CatalogoGeneraleService {

  private readonly baseUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(API_BASE_URL) apiBaseUrl: string
  ) {
    this.baseUrl = this.combineUrl(apiBaseUrl, '/api/catalogo');
    console.log('✅ [CatalogoGeneraleService] Base URL:', this.baseUrl);
  }
  getTutti(): Observable<Libro[]> {
    const url = `${this.baseUrl}/generale`;
    return this.http.get<Libro[]>(url);
  }




  aggiungi(libro: Libro): Observable<Libro> {
    console.log('➕ [CatalogoGeneraleService] POST libro:', libro);
    return this.http.post<Libro>(this.baseUrl, libro);
  }



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
