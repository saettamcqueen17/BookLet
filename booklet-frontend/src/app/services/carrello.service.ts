import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CarrelloDTO } from '../models/carrello';

export interface AggiungiItemRequest {
  isbn: string;
  quantita: number;
}

export interface AggiornaQuantitaRequest {
  quantita: number;
}

@Injectable({ providedIn: 'root' })
export class CarrelloService {

  private apiUrl = `${environment.apiBase}/api/me/carrello`;

  constructor(private http: HttpClient) {
  }

  getCarrello(): Observable<CarrelloDTO> {
    return this.http.get<CarrelloDTO>(this.apiUrl);
  }

  aggiungiAlCarrello(isbn: string, quantita = 1): Observable<CarrelloDTO> {
    const body: AggiungiItemRequest = {isbn, quantita};
    return this.http.post<CarrelloDTO>(`${this.apiUrl}/items`, body);
  }

  aggiornaQuantita(isbn: string, quantita: number): Observable<CarrelloDTO> {
    const body: AggiornaQuantitaRequest = {quantita};
    return this.http.patch<CarrelloDTO>(`${this.apiUrl}/items/${encodeURIComponent(isbn)}`, body);
  }

  rimuoviDalCarrello(isbn: string): Observable<CarrelloDTO> {
    return this.http.delete<CarrelloDTO>(`${this.apiUrl}/items/${encodeURIComponent(isbn)}`);
  }

  svuotaCarrello(): Observable<CarrelloDTO> {
    return this.http.delete<CarrelloDTO>(this.apiUrl);
  }

  checkout(carrelloCorrente: CarrelloDTO) {
    return this.http.post(
      `${this.apiUrl}/checkout`,
      carrelloCorrente,
      {responseType: 'text'}
    );
  }
}
